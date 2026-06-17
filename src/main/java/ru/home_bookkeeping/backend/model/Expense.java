package ru.home_bookkeeping.backend.model;
import java.util.HashSet;
import java.time.LocalDate;

public class Expense {
    private int number;        // Номер по порядку
    private double amount;     // Сумма расхода
    private String category;   // Категория расхода
    private LocalDate date;    // Дата расхода
    private static final HashSet<String> categories = new HashSet<>() {{
        add("Продукты");
        add("Транспорт");
        add("Жильё и коммунальные услуги");
        add("Одежда и обувь");
        add("Здоровье и медицина");
        add("Развлечения");
        add("Образование");
        add("Рестораны и кафе");
        add("Связь и интернет");
        add("Прочее");
    }};

    /**
     * @param number   Порядковый номер записи
     * @param amount   Сумма расхода
     * @param category Категория расхода
     * @param date     Дата расхода
     */
    public Expense(int number, double amount, String category, LocalDate date) {
        this.number   = number;
        this.amount   = amount;
        this.category = category;
        this.date  = date;
    }
    //Нужен пустой конструктор для Gson
    public Expense() {}

    // ───── Геттеры ─────────────────────────────────────────────

    public int getNumber() {
        return number;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
    public LocalDate getDate() {
        return date;
    }

    /** Статический геттер — возвращает доступные категории расходов */
    public static HashSet<String> getCategories() {
        return categories;
}

    // ───── Сеттеры ─────────────────────────────────────────────
    public void setNumber(int number) {
        this.number = number;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(LocalDate date) { this.date = date; }
}
