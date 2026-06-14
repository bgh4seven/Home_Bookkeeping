package ru.home_bookkeeping.backend.model;

import java.util.HashMap;
import java.util.Map;

//Класс нужен для сериализации/десериализации с помощью Gson
public class Database {

    private Map<Integer, Deposit> deposits = new HashMap<>();
    private Map<Integer, Credit> credits = new HashMap<>();
    private Map<Integer, Expense> expenses = new HashMap<>();
    private Map<Integer, Income> incomes = new HashMap<>();


    public Map<Integer, Deposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(Map<Integer, Deposit> deposits) {
        this.deposits = deposits;
    }

    public Map<Integer, Credit> getCredits() {
        return credits;
    }

    public void setCredits(Map<Integer, Credit> credits) {
        this.credits = credits;
    }

    public Map<Integer, Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Map<Integer, Expense> expenses) {
        this.expenses = expenses;
    }

    public Map<Integer, Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(Map<Integer, Income> incomes) {
        this.incomes = incomes;
    }
}