package ru.home_bookkeeping.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.home_bookkeeping.backend.model.Credit;
import ru.home_bookkeeping.backend.model.Deposit;
import ru.home_bookkeeping.backend.model.Expense;
import ru.home_bookkeeping.backend.model.Income;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;


public final class FinancialAnalyzer {
    private final DataBaseController db;

    public FinancialAnalyzer(DataBaseController db) {
        this.db = Objects.requireNonNull(db);
    }


    public Optional<YearMonth> findLatestTransactionMonth() {
        Stream<LocalDate> incomeDates = db.getAllIncomes().stream().map(Income::getDate);
        Stream<LocalDate> expenseDates = db.getAllExpenses().stream().map(Expense::getDate);

        return Stream.concat(incomeDates, expenseDates)
                .filter(Objects::nonNull)
                .map(YearMonth::from)
                .max(Comparator.naturalOrder());
    }


    public JsonObject buildReport(YearMonth selectedMonth) {
        Objects.requireNonNull(selectedMonth, "selectedMonth");

        JsonObject report = new JsonObject();
        report.addProperty("period", selectedMonth.toString());
        report.addProperty("currency", "RUB");

        List<Expense> expenses = db.getAllExpenses();
        JsonArray months = new JsonArray();

        for (int offset = 2; offset >= 0; offset--) {
            YearMonth month = selectedMonth.minusMonths(offset);
            double income = round2(db.getIncomeForMonth(month.getMonthValue(), month.getYear()));
            double spending = round2(db.getExpenseForMonth(month.getMonthValue(), month.getYear()));

            JsonObject monthData = new JsonObject();
            monthData.addProperty("month", month.toString());
            monthData.addProperty("income", income);
            monthData.addProperty("expenses", spending);
            monthData.addProperty("recordedBalance", round2(income - spending));

            Map<String, Double> byCategory = new TreeMap<>();
            for (Expense expense : expenses) {
                LocalDate date = expense.getDate();
                if (date != null && YearMonth.from(date).equals(month)) {
                    String category = expense.getCategory() == null
                            ? "Без категории" : expense.getCategory();
                    byCategory.merge(category, expense.getAmount(), Double::sum);
                }
            }

            JsonObject categoryData = new JsonObject();
            byCategory.forEach((category, amount) ->
                    categoryData.addProperty(category, round2(amount)));
            monthData.add("expensesByCategory", categoryData);
            months.add(monthData);
        }
        report.add("months", months);

        LocalDate lastDay = selectedMonth.atEndOfMonth();
        double scheduledCreditPayments = 0;
        for (Credit credit : db.getAllCredits()) {
            if (isActiveAtMonthEnd(credit.getOpenDate(), credit.getCloseDate(), lastDay)) {
                scheduledCreditPayments += credit.getMonthlyPayment();
            }
        }

        double activeDepositPrincipal = 0;
        for (Deposit deposit : db.getAllDeposits()) {
            if (isActiveAtMonthEnd(deposit.getOpenDate(), deposit.getCloseDate(), lastDay)) {
                activeDepositPrincipal += deposit.getAmount();
            }
        }

        report.addProperty("scheduledMonthlyCreditPayments", round2(scheduledCreditPayments));
        report.addProperty("creditPaymentsIncludedInExpenses", "unknown");
        report.addProperty("activeDepositPrincipal", round2(activeDepositPrincipal));
        return report;
    }

    private static boolean isActiveAtMonthEnd(LocalDate opened, LocalDate closed, LocalDate day) {
        return opened != null
                && !opened.isAfter(day)
                && (closed == null || closed.isAfter(day));
    }

    private static double round2(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
