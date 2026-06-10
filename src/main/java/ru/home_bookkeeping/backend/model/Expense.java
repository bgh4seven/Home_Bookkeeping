package ru.home_bookkeeping.backend.model;
import java.util.HashSet;

public class Expense {
    private int number;        // Номер по порядку
    private double amount;     // Сумма расхода
    private String category;   // Категория расхода
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
     */
    public Expense(int number, double amount, String category) {
        this.number   = number;
        this.amount   = amount;
        this.category = category;
    }

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

    /** Статический геттер — возвращает доступные категории расходов */
    public static HashSet<String> getCategories() {
        return categories;
}
}
