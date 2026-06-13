package ru.home_bookkeeping.backend.model;
import java.time.LocalDate;

public class Credit {
    private int number;              // Номер по порядку
    private String bankName;         // Наименование банка
    private double initialAmount;    // Начальная сумма кредита
    private double remainingAmount;  // Сумма к погашению (сколько осталось выплатить)
    private double monthlyPayment;   // Сумма ежемесячного платежа
    private double percent;          // Процентная ставка (% годовых)
    private int months;              // Срок кредита в месяцах
    private LocalDate openDate;      // Дата получения кредита
    private LocalDate closeDate;     // Дата закрытия (рассчитывается автоматически)
    private double overpayment;      // Итоговая переплата (рассчитывается автоматически)
    /**
     * Конструктор. Принимает основные параметры кредита,
     * автоматически вычисляет дату закрытия и итоговую переплату.
     *
     * @param number         Порядковый номер
     * @param bankName       Наименование банка
     * @param initialAmount  Начальная сумма кредита
     * @param percent        Годовая процентная ставка
     * @param monthlyPayment Сумма ежемесячного платежа
     * @param months         Срок кредита в месяцах
     * @param openDate       Дата получения кредита
     */
    public Credit(int number, String bankName, double initialAmount,
                  double percent, double monthlyPayment,
                  int months, LocalDate openDate) {
        this.number         = number;
        this.bankName       = bankName;
        this.initialAmount  = initialAmount;
        this.percent        = percent;
        this.monthlyPayment = monthlyPayment;
        this.months         = months;
        this.openDate       = openDate;

        // Изначально вся сумма подлежит погашению
        this.remainingAmount = initialAmount;

        // Дата закрытия: дата получения + срок в месяцах
        this.closeDate = openDate.plusMonths(months);

        // Переплата = ежемесячный_платёж × срок - начальная_сумма
        this.overpayment = monthlyPayment * months - initialAmount;
    }

    // ───── Геттеры ─────────────────────────────────────────────

    public int getNumber() {
        return number;
    }

    public String getBankName() {
        return bankName;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public double getPercent() {
        return percent;
    }

    public int getMonths() {
        return months;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public double getOverpayment() {
        return overpayment;
    }

    // ───── Методы ──────────────────────────────────────────────

    /**
     * Вносит минимальный ежемесячный платёж.
     * Уменьшает сумму к погашению на размер ежемесячного платежа.
     */
    public void payMonthly() {
        remainingAmount -= monthlyPayment;
    }
}

