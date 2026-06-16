package ru.home_bookkeeping.backend.model;

import java.time.LocalDate;

public class Deposit {

    private int number;          // Номер по порядку
    private String bankName;     // Наименование банка
    private double amount;       // Сумма вклада
    private double percent;      // Процентная ставка (% годовых)
    private int months;            // Срок вклада в месяцах
    private LocalDate openDate;  // Дата открытия
    private LocalDate closeDate; // Дата закрытия (рассчитывается автоматически)
    private double income;       // Ожидаемый доход (рассчитывается автоматически)

    /**
     * Конструктор. Принимает основные параметры вклада,
     * автоматически вычисляет дату закрытия и ожидаемый доход.
     *
     * @param number   Порядковый номер вклада
     * @param bankName Наименование банка
     * @param amount   Сумма вклада (в рублях)
     * @param percent  Годовая процентная ставка
     * @param months     Срок вклада в месяцах
     * @param openDate Дата открытия вклада
     */
    public Deposit(int number, String bankName, double amount,
                   double percent, int months, LocalDate openDate) {
        this.number   = number;
        this.bankName = bankName;
        this.amount   = amount;
        this.percent  = percent;
        this.months     = months;
        this.openDate = openDate;

        // Расчёт даты закрытия: дата открытия + срок в днях
        this.closeDate = openDate.plusMonths(months);

        // Расчёт ожидаемого дохода по формуле простых процентов:
        // income = amount * (percent / 100) * (months / 12)
        this.income = amount * (percent / 100.0) * (months / 12.0);
    }
    public Deposit() {
        // пустой конструктор нужен для Gson
    }

    // ───── Геттеры ─────────────────────────────────────────────

    /** Возвращает порядковый номер вклада */
    public int getNumber() {
        return number;
    }

    /** Возвращает наименование банка */
    public String getBankName() {
        return bankName;
    }

    /** Возвращает сумму вклада */
    public double getAmount() {
        return amount;
    }

    /** Возвращает процентную ставку */
    public double getPercent() {
        return percent;
    }

    /** Возвращает срок вклада в днях */
    public int getMonths() {
        return months;
    }

    /** Возвращает дату открытия */
    public LocalDate getOpenDate() {
        return openDate;
    }

    /** Возвращает дату закрытия (рассчитана автоматически) */
    public LocalDate getCloseDate() {
        return closeDate;
    }

    /** Возвращает ожидаемый доход (рассчитан автоматически) */
    public double getIncome() {
        return income;
    }

    // ───── Сеттеры ─────────────────────────────────────────────
    public void setNumber(int number) {
        this.number = number;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
