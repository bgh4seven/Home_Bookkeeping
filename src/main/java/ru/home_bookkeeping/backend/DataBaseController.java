package ru.home_bookkeeping.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.home_bookkeeping.backend.model.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataBaseController {

    //Путь к файлам баз данных
    private Path dbPath = Paths.get("db.json");
    //Сюда подгружается дб во время работы программы
    private Database db = readDB(dbPath);
    private Map<Integer, Deposit> deposits = db.getDeposits();
    private Map<Integer, Credit> credits = db.getCredits();
    private Map<Integer, Income> incomes = db.getIncomes();
    private Map<Integer, Expense> expenses = db.getExpenses();


    //Создание Gson для работы с db (общий для всех db)
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();


    public void addDeposit(int number, String bankName, double amount,
                           double percent, int months, LocalDate openDate) {
        Deposit deposit = new Deposit(number, bankName, amount, percent, months, openDate);
        deposits.put(number, deposit);
    }

    public void deleteDeposit(int number) {
        deposits.remove(number);
    }

    // Возвращает все вклады в виде коллекции (для таблицы)
    public List<Deposit> getAllDeposits() {
        return new ArrayList<>(deposits.values());
    }

    // Возвращает следующий свободный номер вклада. Нужен, чтобы вклады создавались с разными номерами
    public int getNextDepositNumber() {
        if (deposits.isEmpty()) return 1;
        int max = 0;
        for (int key : deposits.keySet()) {
            if (key > max) max = key;
        }
        return max + 1;
    }
    // Считает общую сумму всех вкладов
    public double getTotalDeposits() {
        double total = 0;
        for (Deposit deposit : deposits.values()) {
            total += deposit.getAmount();
        }
        return total;
    }

    public void addCredit(int number, String bankName, double initialAmount,
                          double percent, double monthlyPayment, int months, LocalDate openDate) {
        Credit credit = new Credit(number, bankName, initialAmount, percent, monthlyPayment, months, openDate);
        credits.put(number, credit);
    }

    public void deleteCredit(int number) {
        credits.remove(number);
    }

    public List<Credit> getAllCredits() {
        return new ArrayList<>(credits.values());
    }

    public int getNextCreditNumber() {
        if (credits.isEmpty()) return 1;
        int max = 0;
        for (int key : credits.keySet()) {
            if (key > max) max = key;
        }
        return max + 1;
    }

    // Считает общую задолженность по всем кредитам (сумма remainingAmount)
    public double getTotalCreditDebt() {
        double total = 0;
        for (Credit credit : credits.values()) {
            total += credit.getRemainingAmount();
        }
        return total;
    }

    public void addIncome(int number, double amount, String source, LocalDate date) {

        Income income = new Income(number, amount, source, date);
        incomes.put(number, income);
    }

    public void deleteIncome(int number) {
        incomes.remove(number);
    }

    public List<Income> getAllIncomes() {
        return new ArrayList<>(incomes.values());
    }

    public int getNextIncomeNumber() {
        if (incomes.isEmpty()) return 1;
        int max = 0;
        for (int key : incomes.keySet()) {
            if (key > max) max = key;
        }
        return max + 1;
    }
    // Считает сумму доходов за определённый месяц и год
    public double getIncomeForMonth(int month, int year) {
        double total = 0;
        for (Income income : incomes.values()) {
            if (income.getDate().getMonthValue() == month
                    && income.getDate().getYear() == year) {
                total += income.getAmount();
            }
        }
        return total;
    }

    public void addExpense(int number, double amount, String category, LocalDate date) {
        Expense expense = new Expense(number, amount, category, date);
        expenses.put(number, expense);
    }

    public void deleteExpense(int number) {
        expenses.remove(number);
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses.values());
    }

    public int getNextExpenseNumber() {
        if (expenses.isEmpty()) return 1;
        int max = 0;
        for (int key : expenses.keySet()) {
            if (key > max) max = key;
        }
        return max + 1;
    }
    // Считает сумму расходов за определённый месяц и год
    public double getExpenseForMonth(int month, int year) {
        double total = 0;
        for (Expense expense : expenses.values()) {
            if (expense.getDate().getMonthValue() == month
                    && expense.getDate().getYear() == year) {
                total += expense.getAmount();
            }
        }
        return total;
    }

    private Database readDB(Path path) {
        Database holder = new Database();
        try (FileReader reader = new FileReader(path.toFile())) {
            holder = gson.fromJson(reader, Database.class);
        } catch (FileNotFoundException e) {
            System.out.println("No existing DB, created new");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return holder;
    }


    public void writeDB() {
        try (FileWriter writer = new FileWriter(dbPath.toFile())) {
            String db = gson.toJson(this.db);
            writer.write(db);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Ошибка записи DB... " + e.getMessage());
            System.out.println("Изменения не были сохранены");
        }
    }





}
