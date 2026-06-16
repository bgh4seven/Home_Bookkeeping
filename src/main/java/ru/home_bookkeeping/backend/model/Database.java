package ru.home_bookkeeping.backend.model;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

//Класс нужен для сериализации/десериализации с помощью Gson
public class Database {

    private Map<Integer, Deposit> deposits = new HashMap<>();
    private Map<Integer, Credit> credits = new HashMap<>();
    private Map<Integer, Expense> expenses = new HashMap<>();
    private Map<Integer, Income> incomes = new HashMap<>();
    /**
     * Создаёт новый объект Deposit и сохраняет его в Map.
     *
     * @param number   Порядковый номер вклада (ключ в Map)
     * @param bankName Наименование банка
     * @param amount   Сумма вклада (в рублях)
     * @param percent  Годовая процентная ставка
     * @param months   Срок вклада в месяцах
     * @param openDate Дата открытия вклада
     */
    public void addDeposit(int number, String bankName, double amount,
                           double percent, int months, LocalDate openDate) {
        Deposit deposit = new Deposit(number, bankName, amount, percent, months, openDate);
        deposits.put(number, deposit);
    }
    /**
     * Возвращает объект Deposit из Map по порядковому номеру.
     * Если вклад с таким номером не найден — возвращает null.
     *
     * @param number Порядковый номер вклада
     * @return Deposit или null
     */
    public Deposit getDeposit(int number) {
        return deposits.get(number);
    }
    public Map<Integer, Deposit> getDeposits() {
        return deposits;
    }

    // ───── Кредиты ─────────────────────────────────────────────

    /**
     * Создаёт новый объект Credit и сохраняет его в Map.
     *
     * @param number         Порядковый номер кредита (ключ в Map)
     * @param bankName       Наименование банка
     * @param initialAmount  Начальная сумма кредита
     * @param percent        Годовая процентная ставка
     * @param monthlyPayment Сумма ежемесячного платежа
     * @param months         Срок кредита в месяцах
     * @param openDate       Дата получения кредита
     */
    public void addCredit(int number, String bankName, double initialAmount,
                          double percent, double monthlyPayment,
                          int months, LocalDate openDate) {
        Credit credit = new Credit(number, bankName, initialAmount, percent,
                monthlyPayment, months, openDate);
        credits.put(number, credit);
    }
    /**
     * Возвращает объект Credit из Map по порядковому номеру.
     * Если кредит с таким номером не найден — возвращает null.
     *
     * @param number Порядковый номер кредита
     * @return Credit или null
     */
    public Credit getCredit(int number) {
        return credits.get(number);
    }
    public Map<Integer, Credit> getCredits() {
        return credits;
    }

// ───── Доходы ──────────────────────────────────────────────

    /**
     * Создаёт новый объект Income и сохраняет его в Map.
     *
     * @param number Порядковый номер дохода (ключ в Map)
     * @param amount Сумма дохода
     * @param source Источник дохода
     * @param date   Дата получения дохода
     */
    public void addIncome(int number, double amount, String source, LocalDate date) {
        Income income = new Income(number, amount, source, date);
        incomes.put(number, income);
    }
    /**
     * Возвращает объект Income из Map по порядковому номеру.
     * Если доход с таким номером не найден — возвращает null.
     *
     * @param number Порядковый номер дохода
     * @return Income или null
     */
    public Income getIncome(int number) {
        return incomes.get(number);
    }
    /** Возвращает Map всех доходов */
    public Map<Integer, Income> getIncomes() {
        return incomes;
    }
    // ───── Расходы ─────────────────────────────────────────────

    /**
     * Создаёт новый объект Expense и сохраняет его в Map.
     *
     * @param number   Порядковый номер расхода (ключ в Map)
     * @param amount   Сумма расхода
     * @param category Категория расхода
     * @param date     Дата расхода
     */
    public void addExpense(int number, double amount, String category, LocalDate date) {
        Expense expense = new Expense(number, amount, category, date);
        expenses.put(number, expense);
    }
    /**
     * Возвращает объект Expense из Map по порядковому номеру.
     * Если расход с таким номером не найден — возвращает null.
     *
     * @param number Порядковый номер расхода
     * @return Expense или null
     */
    public Expense getExpense(int number) {
        return expenses.get(number);
    }
    /** Возвращает Map всех расходов */
    public Map<Integer, Expense> getExpenses() {
        return expenses;
    }
}