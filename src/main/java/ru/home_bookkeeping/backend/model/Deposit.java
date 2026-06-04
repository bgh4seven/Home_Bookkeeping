package ru.home_bookkeeping.backend.model;

import java.time.LocalDate;

public class Deposit {

    private int number;          // Номер по порядку
    private String bankName;     // Наименование банка
    private double amount;       // Сумма вклада
    private double percent;      // Процентная ставка (% годовых)
    private int days;            // Срок вклада в днях
    private LocalDate openDate;  // Дата открытия
    private LocalDate closeDate; // Дата закрытия (рассчитывается автоматически)
    private double income;       // Ожидаемый доход (рассчитывается автоматически)

    public void setBankName(String s) {
        this.bankName = s;
    }

    /**
     * Конструктор. Принимает основные параметры вклада,
     * автоматически вычисляет дату закрытия и ожидаемый доход.
     *
     * @param number   Порядковый номер вклада
     * @param bankName Наименование банка
     * @param amount   Сумма вклада (в рублях)
     * @param percent  Годовая процентная ставка
     * @param days     Срок вклада в днях
     * @param openDate Дата открытия вклада
     */
    public Deposit(int number, String bankName, double amount,
                   double percent, int days, LocalDate openDate) {
        this.number   = number;
        this.bankName = bankName;
        this.amount   = amount;
        this.percent  = percent;
        this.days     = days;
        this.openDate = openDate;

        // Расчёт даты закрытия: дата открытия + срок в днях
        this.closeDate = openDate.plusDays(days);

        // Расчёт ожидаемого дохода по формуле простых процентов:
        // income = amount * (percent / 100) * (days / 365)
        this.income = amount * (percent / 100.0) * (days / 365.0);
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
    public int getDays() {
        return days;
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
}
