package ru.home_bookkeeping.backend.model;
import java.util.HashSet;

public class Income {
        private int number;      // Номер по порядку
        private double amount;   // Сумма дохода
        private String source;   // Источник дохода

        // Статичный набор доступных источников дохода на выбор
        private static final HashSet<String> sources = new HashSet<>() {{
            add("Зарплата");
            add("Фриланс");
            add("Подработка");
            add("Вклад");
            add("Аренда");
            add("Продажа имущества");
            add("Пособие / Стипендия");
            add("Подарок");
            add("Прочее");
        }};

        /**
         * @param number Порядковый номер записи
         * @param amount Сумма дохода
         * @param source Источник дохода
         */
        public Income(int number, double amount, String source) {
            this.number = number;
            this.amount = amount;
            this.source = source;
        }
    //Нужен пустой конструктор для Gson
    public Income() {}

        // ───── Геттеры ─────────────────────────────────────────────

        public int getNumber() {
            return number;
        }

        public double getAmount() {
            return amount;
        }

        public String getSource() {
            return source;
        }

        /**
         * Статический геттер — возвращает доступные источники дохода
         */
        public static HashSet<String> getSources() {
            return sources;
        }

    // ───── Сеттеры ─────────────────────────────────────────────
    public void setNumber(int number) {
        this.number = number;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setSource(String source) {
        this.source = source;
    }
    }
