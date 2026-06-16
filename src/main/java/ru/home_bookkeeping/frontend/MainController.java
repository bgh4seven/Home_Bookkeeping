package ru.home_bookkeeping.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import ru.home_bookkeeping.backend.DataBaseController;
import ru.home_bookkeeping.backend.model.Credit;
import ru.home_bookkeeping.backend.model.Deposit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.home_bookkeeping.backend.model.Expense;
import ru.home_bookkeeping.backend.model.Income;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

//Объект данного класса создается в fxml, в Main достаточно обозначить загрузку fxml
public class MainController {

    private DataBaseController db = new DataBaseController();
    //region Поля FXML
    @FXML private Button editModeToggleButton;
    // Сама таблица
    @FXML
    private TableView<Deposit> depositTable;
    // Столбцы таблицы (имена должны совпадать с fx:id, заданными в Scene Builder)
    @FXML private TableColumn<Deposit, Integer> dep_numberColumn;
    @FXML private TableColumn<Deposit, String> dep_bankColumn;          // Название банка
    @FXML private TableColumn<Deposit, Double> dep_amountColumn;
    @FXML private TableColumn<Deposit, Double> dep_percentColumn;       // Процент (например, 8.5)
    @FXML private TableColumn<Deposit, Integer> dep_srokColumn;         // Срок вклада (в месяцах или днях)
    @FXML private TableColumn<Deposit, LocalDate> dep_startDateColumn;  // Дата открытия
    @FXML private TableColumn<Deposit, LocalDate> dep_endDateColumn;    // Дата погашения
    @FXML private TableColumn<Deposit, Double> dep_expectedIncomeColumn; // Ожидаемая доходность
    @FXML private Label depositTotalLabel;
    @FXML private PieChart depositBankChart;
    @FXML private PieChart depositIncomeChart;
    @FXML private Button addDepositButton;    // Кнопка "Добавить вклад"
    @FXML private Button deleteDepositButton; // Кнопка "Удалить вклад"

    @FXML private TableView<Credit> creditTable;
    @FXML private TableColumn<Credit, Integer> cre_numberColumn;
    @FXML private TableColumn<Credit, String>  cre_bankColumn;
    @FXML private TableColumn<Credit, Double>  cre_initialAmountColumn;   // сумма кредита
    @FXML private TableColumn<Credit, Double>  cre_percentColumn;
    @FXML private TableColumn<Credit, Double>  cre_monthlyPaymentColumn;  // ежемесячный платёж
    @FXML private TableColumn<Credit, Integer> cre_monthsColumn;          // срок в месяцах
    @FXML private TableColumn<Credit, LocalDate> cre_openDateColumn;
    @FXML private TableColumn<Credit, LocalDate> cre_closeDateColumn;
    @FXML private TableColumn<Credit, Double>  cre_overpaymentColumn;
    @FXML private TableColumn<Credit, Double> cre_remainingAmountColumn;
    @FXML private Label creditTotalLabel;
    @FXML private PieChart creditBankChart;
    @FXML private PieChart creditOverpaymentChart;
    @FXML private Button addCreditButton;
    @FXML private Button deleteCreditButton;
    @FXML private Button payCreditButton;

    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, Integer> inc_numberColumn;
    @FXML private TableColumn<Income, Double>  inc_amountColumn;
    @FXML private TableColumn<Income, String>  inc_sourceColumn;   // источник дохода
    @FXML private Button addIncomeButton;
    @FXML private Button deleteIncomeButton;
    @FXML private ComboBox<Integer> inc_yearComboBox;
    @FXML private ComboBox<String> inc_monthComboBox;
    @FXML private Label inc_TotalLabel;
    @FXML private BarChart<Number, String> incomeChart;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, Integer> exp_numberColumn;
    @FXML private TableColumn<Expense, Double>  exp_amountColumn;
    @FXML private TableColumn<Expense, String>  exp_categoryColumn; // категория расхода
    @FXML private Button addExpenseButton;
    @FXML private Button deleteExpenseButton;
    @FXML private ComboBox<Integer> exp_YearComboBox;
    @FXML private ComboBox<String> exp_MonthComboBox;
    @FXML private Label exp_TotalLabel;
    @FXML private BarChart<Number, String> expenseChart;
    //endregion

    /**
     * Этот метод вызывается автоматически после загрузки fxml.
     * setCellValueFactory — настраивает, откуда столбец берёт значения для каждой строки.
     * new PropertyValueFactory<>("bankName") — у объекта-поставщика вызывает метод getBankName() и берет значение для ячейки (геттер ищется автоматически)
     */
    @FXML
    public void initialize() {
        // Инициализация таблиц
        initializeDeposit();
        initializeCredit();
        initializeIncome();
        initializeExpense();
        setupEditModeForAllTables();


        editModeToggleButton.setOnAction(event -> {
            boolean current = depositTable.isEditable(); // можно по любой таблице
            setEditMode(!current);
        });
    }

    private void initializeDeposit() {
        dep_numberColumn.setCellValueFactory((new PropertyValueFactory<>("number")));
        dep_bankColumn.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        dep_amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dep_percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        dep_srokColumn.setCellValueFactory(new PropertyValueFactory<>("months"));
        dep_startDateColumn.setCellValueFactory(new PropertyValueFactory<>("openDate"));
        dep_endDateColumn.setCellValueFactory(new PropertyValueFactory<>("closeDate"));
        dep_expectedIncomeColumn.setCellValueFactory(new PropertyValueFactory<>("income"));

        addDepositButton.setOnAction(event -> {
            showAddDepositDialog();
            //refreshDepositTable();
        });
        deleteDepositButton.setOnAction(event -> {
            deleteSelectedDeposit();
            //refreshDepositTable();
        });

        refreshDepositTable();
    }
    private void refreshDepositTable() {
        depositTable.getItems().clear();
        depositTable.getItems().addAll(db.getAllDeposits());
        double total = db.getTotalDeposits();
        depositTotalLabel.setText(String.format("Общая сумма вкладов: %.2f руб", total));
        updateDepositCharts();
    }
    private void deleteSelectedDeposit() {
        Deposit selected = depositTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteDeposit(selected.getNumber());
            db.writeDB();
            refreshDepositTable();
        } else {
            showAlert("Удаление вклада", "Выберите вклад для удаления");
        }
    }
    private void showAddDepositDialog()  {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-deposit-dialog.fxml"));
            Parent root = loader.load();

            // Получаем контроллер диалога
            AddDepositController dialogController = loader.getController();

            // Создаём новое окно (Stage)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Новый вклад");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false); // чтобы окно не меняло размер

            // Ждём, пока окно закроется
            dialogStage.showAndWait();

            // После закрытия проверяем, создан ли вклад
            Deposit newDeposit = dialogController.getCreatedDeposit();
            if (newDeposit != null) {
                int newNumber = db.getNextDepositNumber();
                // Создаём вклад с правильным номером
                db.addDeposit(newNumber, newDeposit.getBankName(), newDeposit.getAmount(),
                        newDeposit.getPercent(), newDeposit.getMonths(), newDeposit.getOpenDate());
                db.writeDB();
                refreshDepositTable();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Не удалось загрузить окно добавления вклада");
            alert.showAndWait();
        }
    }
    private void updateDepositCharts() {
        List<Deposit> deposits = db.getAllDeposits();

        // 1. Диаграмма по банкам
        Map<String, Double> bankSum = new HashMap<>();
        for (Deposit d : deposits) {
            bankSum.put(d.getBankName(), bankSum.getOrDefault(d.getBankName(), 0.0) + d.getAmount());
        }
        depositBankChart.getData().clear();
        if (bankSum.isEmpty()) {
            depositBankChart.getData().add(new PieChart.Data("Нет данных", 1));
        } else {
            for (Map.Entry<String, Double> entry : bankSum.entrySet()) {
                depositBankChart.getData().add(new PieChart.Data(entry.getKey() + " (" + String.format("%.0f", entry.getValue()) + " руб)", entry.getValue()));
            }
        }

        // 2. Диаграмма доходности по вкладам
        depositIncomeChart.getData().clear();
        if (deposits.isEmpty()) {
            depositIncomeChart.getData().add(new PieChart.Data("Нет данных", 1));
        } else {
            for (Deposit d : deposits) {
                String label = "Вклад #" + d.getNumber() + " (" + String.format("%.0f", d.getIncome()) + " руб)";
                depositIncomeChart.getData().add(new PieChart.Data(label, d.getIncome()));
            }
        }
    }

    private void initializeCredit() {
        cre_numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        cre_bankColumn.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        cre_initialAmountColumn.setCellValueFactory(new PropertyValueFactory<>("initialAmount"));
        cre_percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        cre_monthlyPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyPayment"));
        cre_monthsColumn.setCellValueFactory(new PropertyValueFactory<>("months"));
        cre_openDateColumn.setCellValueFactory(new PropertyValueFactory<>("openDate"));
        cre_closeDateColumn.setCellValueFactory(new PropertyValueFactory<>("closeDate"));
        cre_overpaymentColumn.setCellValueFactory(new PropertyValueFactory<>("overpayment"));
        cre_remainingAmountColumn.setCellValueFactory(new PropertyValueFactory<>("remainingAmount"));

        addCreditButton.setOnAction(event -> {
            showAddCreditDialog();
            //refreshCreditTable();
        });
        deleteCreditButton.setOnAction(event -> {
            deleteSelectedCredit();
            //refreshCreditTable();
        });
        payCreditButton.setOnAction(event -> makeCreditPayment());

        refreshCreditTable();
    }
    private void refreshCreditTable() {
        creditTable.getItems().clear();
        creditTable.getItems().addAll(db.getAllCredits());
        double totalDebt = db.getTotalCreditDebt();
        creditTotalLabel.setText(String.format("Общая задолженность: %.2f руб", totalDebt));
        updateCreditCharts();
    }
    private void deleteSelectedCredit() {
        Credit selected = creditTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteCredit(selected.getNumber());
            db.writeDB();
            refreshCreditTable();
        } else {
            showAlert("Удаление кредита", "Выберите кредит для удаления");
        }
    }
    private void showAddCreditDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-credit-dialog.fxml"));
            Parent root = loader.load();
            AddCreditController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Новый кредит");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            Credit newCredit = controller.getCreatedCredit();
            if (newCredit != null) {
                int newNumber = db.getNextCreditNumber();
                db.addCredit(newNumber, newCredit.getBankName(), newCredit.getInitialAmount(),
                        newCredit.getPercent(), newCredit.getMonthlyPayment(),
                        newCredit.getMonths(), newCredit.getOpenDate());
                db.writeDB();
                refreshCreditTable();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно добавления кредита");
        }
    }
    private void makeCreditPayment() {
        Credit selected = creditTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.payMonthly();  // уменьшает remainingAmount
            db.writeDB();           // сохраняем изменения
            refreshCreditTable();   // обновляем таблицу и метку
        } else {
            showAlert("Оплата кредита", "Выберите кредит для внесения платежа");
        }
    }
    private void updateCreditCharts() {
        List<Credit> credits = db.getAllCredits();

        // 1. Диаграмма по банкам (остаток)
        Map<String, Double> bankRemaining = new HashMap<>();
        for (Credit c : credits) {
            bankRemaining.put(c.getBankName(), bankRemaining.getOrDefault(c.getBankName(), 0.0) + c.getRemainingAmount());
        }
        creditBankChart.getData().clear();
        if (bankRemaining.isEmpty()) {
            creditBankChart.getData().add(new PieChart.Data("Нет данных", 1));
        } else {
            for (Map.Entry<String, Double> entry : bankRemaining.entrySet()) {
                creditBankChart.getData().add(new PieChart.Data(entry.getKey() + " (" + String.format("%.0f", entry.getValue()) + " руб)", entry.getValue()));
            }
        }

        // 2. Диаграмма переплаты по кредитам
        creditOverpaymentChart.getData().clear();
        if (credits.isEmpty()) {
            creditOverpaymentChart.getData().add(new PieChart.Data("Нет данных", 1));
        } else {
            for (Credit c : credits) {
                String label = "Кредит #" + c.getNumber() + " (" + String.format("%.0f", c.getOverpayment()) + " руб)";
                creditOverpaymentChart.getData().add(new PieChart.Data(label, c.getOverpayment()));
            }
        }
    }

    private void initializeIncome() {
        inc_numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        inc_amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        inc_sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));


        addIncomeButton.setOnAction(event -> {
            showAddIncomeDialog();
            //refreshIncomeTable();
        });
        deleteIncomeButton.setOnAction(event -> {
            deleteSelectedIncome();
            //refreshIncomeTable();
        });

        // Настройка выбора года и месяца
        inc_yearComboBox.getItems().addAll(2023, 2024, 2025, 2026); // можно динамически вычислять
        inc_yearComboBox.setValue(LocalDate.now().getYear());
        inc_monthComboBox.getItems().addAll("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь");
        inc_monthComboBox.setValue(monthName(LocalDate.now().getMonthValue()));

        // Слушатели
        inc_yearComboBox.setOnAction(e -> updateIncomeChart());
        inc_monthComboBox.setOnAction(e -> updateIncomeChart());

        // Первоначальная отрисовка
        updateIncomeChart();

        refreshIncomeTable();
    }
    private String monthName(int month) {
        String[] names = {"Январь","Февраль","Март","Апрель","Май","Июнь",
                "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
        return names[month-1];
    }
    private void refreshIncomeTable() {
        incomeTable.getItems().clear();
        incomeTable.getItems().addAll(db.getAllIncomes());
        updateIncomeChart();
    }
    private void updateIncomeChart() {
        int year = inc_yearComboBox.getValue();
        String monthName = inc_monthComboBox.getValue();
        int month = monthNameToNumber(monthName);

        // 1. Получаем общую сумму из бэкенда
        double total = db.getIncomeForMonth(month, year);
        inc_TotalLabel.setText(String.format("Итого: %.2f руб", total));

        // 2. Строим гистограмму
        List<Income> all = db.getAllIncomes();
        Map<String, Double> sourceSum = new HashMap<>();

        for (Income inc : all) {
            LocalDate d = inc.getDate();
            if (d != null && d.getYear() == year && d.getMonthValue() == month) {
                String src = inc.getSource();
                sourceSum.put(src, sourceSum.getOrDefault(src, 0.0) + inc.getAmount());
            }
        }

        incomeChart.getData().clear();

        for (Map.Entry<String, Double> entry : sourceSum.entrySet()) {
            String category = entry.getKey();
            double sum = entry.getValue();
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName(category);
            series.getData().add(new XYChart.Data<>(sum, category));
            incomeChart.getData().add(series);
        }

        if (sourceSum.isEmpty()) {
            XYChart.Series<Number, String> empty = new XYChart.Series<>();
            empty.setName("Нет данных");
            empty.getData().add(new XYChart.Data<>(0, "Нет данных"));
            incomeChart.getData().add(empty);
        }
    }
    private int monthNameToNumber(String name) {
        String[] names = {"Январь","Февраль","Март","Апрель","Май","Июнь",
                "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) return i+1;
        }
        return 1;
    }
    private void deleteSelectedIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteIncome(selected.getNumber());
            db.writeDB();
            refreshIncomeTable();
        } else {
            showAlert("Удаление дохода", "Выберите доход для удаления");
        }
    }
    private void showAddIncomeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-income-dialog.fxml"));
            Parent root = loader.load();
            AddIncomeController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Новый доход");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            Income newIncome = controller.getCreatedIncome();
            if (newIncome != null) {
                int newNumber = db.getNextIncomeNumber();
                db.addIncome(newNumber, newIncome.getAmount(), newIncome.getSource(), newIncome.getDate());
                db.writeDB();
                refreshIncomeTable();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно добавления дохода");
        }
    }

    private void initializeExpense() {
        exp_numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        exp_amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        exp_categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        addExpenseButton.setOnAction(event -> {
            showAddExpenseDialog();
            //refreshExpenseTable();
        });
        deleteExpenseButton.setOnAction(event -> {
            deleteSelectedExpense();
            //refreshExpenseTable();
        });

        // Настройка выбора года и месяца
        exp_YearComboBox.getItems().addAll(2023, 2024, 2025, 2026); // или динамически
        exp_YearComboBox.setValue(LocalDate.now().getYear());
        exp_MonthComboBox.getItems().addAll("Январь","Февраль","Март","Апрель","Май","Июнь",
                "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь");
        exp_MonthComboBox.setValue(monthName(LocalDate.now().getMonthValue()));

        exp_YearComboBox.setOnAction(e -> updateExpenseChart());
        exp_MonthComboBox.setOnAction(e -> updateExpenseChart());

        refreshExpenseTable();
    }
    private void refreshExpenseTable() {
        expenseTable.getItems().clear();
        expenseTable.getItems().addAll(db.getAllExpenses());
        updateExpenseChart();
    }
    private void updateExpenseChart() {
        int year = exp_YearComboBox.getValue();
        String monthName = exp_MonthComboBox.getValue();
        int month = monthNameToNumber(monthName);

        // Получаем общую сумму из бэкенда
        double total = db.getExpenseForMonth(month, year);
        exp_TotalLabel.setText(String.format("Итого: %.2f руб", total));

        // Строим гистограмму
        List<Expense> all = db.getAllExpenses();
        Map<String, Double> categorySum = new HashMap<>();

        for (Expense exp : all) {
            LocalDate d = exp.getDate();
            if (d != null && d.getYear() == year && d.getMonthValue() == month) {
                String cat = exp.getCategory();
                categorySum.put(cat, categorySum.getOrDefault(cat, 0.0) + exp.getAmount());
            }
        }

        expenseChart.getData().clear();

        for (Map.Entry<String, Double> entry : categorySum.entrySet()) {
            String category = entry.getKey();
            double sum = entry.getValue();
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName(category);
            series.getData().add(new XYChart.Data<>(sum, category));
            expenseChart.getData().add(series);
        }

        if (categorySum.isEmpty()) {
            XYChart.Series<Number, String> empty = new XYChart.Series<>();
            empty.setName("Нет данных");
            empty.getData().add(new XYChart.Data<>(0, "Нет данных"));
            expenseChart.getData().add(empty);
        }
    }
    private void deleteSelectedExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteExpense(selected.getNumber());
            db.writeDB();
            refreshExpenseTable();
        } else {
            showAlert("Удаление расхода", "Выберите расход для удаления");
        }
    }
    private void showAddExpenseDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-expense-dialog.fxml"));
            Parent root = loader.load();
            AddExpenseController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Новый расход");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            Expense newExpense = controller.getCreatedExpense();
            if (newExpense != null) {
                int newNumber = db.getNextExpenseNumber();
                db.addExpense(newNumber, newExpense.getAmount(), newExpense.getCategory(), newExpense.getDate());
                db.writeDB();
                refreshExpenseTable();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно добавления расхода");
        }
    }

    //всплывающее предупреждение
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    //region EditMode
    private void setupEditModeForAllTables() {
        setupDepositEdit();
        setupCreditEdit();
        setupIncomeEdit();
        setupExpenseEdit();

        // Изначально редактирование выключено
        depositTable.setEditable(false);
        creditTable.setEditable(false);
        incomeTable.setEditable(false);
        expenseTable.setEditable(false);
    }

    private void setupDepositEdit() {
        // Банк (String) — работает без конвертера
        dep_bankColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dep_bankColumn.setOnEditCommit(event -> {
            Deposit d = event.getRowValue();
            d.setBankName(event.getNewValue());
            db.writeDB();
            refreshDepositTable();
        });

        // Сумма (Double)
        dep_amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        dep_amountColumn.setOnEditCommit(event -> {
            Deposit d = event.getRowValue();
            d.setAmount(event.getNewValue());
            db.writeDB();
            refreshDepositTable();
        });

        // Процент (Double)
        dep_percentColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        dep_percentColumn.setOnEditCommit(event -> {
            Deposit d = event.getRowValue();
            d.setPercent(event.getNewValue());
            db.writeDB();
            refreshDepositTable();
        });

        // Срок в днях (Integer)
        dep_srokColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        dep_srokColumn.setOnEditCommit(event -> {
            Deposit d = event.getRowValue();
            int newMonths = event.getNewValue();
            d.setMonths(newMonths);
            // Пересчёт даты закрытия и дохода
            d.setCloseDate(d.getOpenDate().plusDays(newMonths));
            d.setIncome(d.getAmount() * (d.getPercent() / 100.0) * (newMonths / 12.0));
            db.writeDB();
            refreshDepositTable();
        });
    }

    private void setupCreditEdit() {
        // Банк (String)
        cre_bankColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cre_bankColumn.setOnEditCommit(event -> {
            Credit c = event.getRowValue();
            c.setBankName(event.getNewValue());
            db.writeDB();
            refreshCreditTable();
        });

        // Сумма кредита (Double)
        cre_initialAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cre_initialAmountColumn.setOnEditCommit(event -> {
            Credit c = event.getRowValue();
            c.setInitialAmount(event.getNewValue());
            db.writeDB();
            refreshCreditTable();
        });

        // Процент (Double)
        cre_percentColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cre_percentColumn.setOnEditCommit(event -> {
            Credit c = event.getRowValue();
            c.setPercent(event.getNewValue());
            db.writeDB();
            refreshCreditTable();
        });

        // Ежемесячный платёж (Double)
        cre_monthlyPaymentColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cre_monthlyPaymentColumn.setOnEditCommit(event -> {
            Credit c = event.getRowValue();
            c.setMonthlyPayment(event.getNewValue());
            db.writeDB();
            refreshCreditTable();
        });

        // Срок в месяцах (Integer)
        cre_monthsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cre_monthsColumn.setOnEditCommit(event -> {
            Credit c = event.getRowValue();
            int newMonths = event.getNewValue();
            c.setMonths(newMonths);
            // Пересчёт даты закрытия и переплаты
            c.setCloseDate(c.getOpenDate().plusMonths(newMonths));
            c.setOverpayment(c.getMonthlyPayment() * newMonths - c.getInitialAmount());
            db.writeDB();
            refreshCreditTable();
        });
    }

    private void setupIncomeEdit() {
        // Сумма (Double)
        inc_amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        inc_amountColumn.setOnEditCommit(event -> {
            Income i = event.getRowValue();
            i.setAmount(event.getNewValue());
            db.writeDB();
            refreshIncomeTable();
        });

        // Источник (String)
        inc_sourceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        inc_sourceColumn.setOnEditCommit(event -> {
            Income i = event.getRowValue();
            i.setSource(event.getNewValue());
            db.writeDB();
            refreshIncomeTable();
        });
    }

    private void setupExpenseEdit() {
        // Сумма (Double)
        exp_amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        exp_amountColumn.setOnEditCommit(event -> {
            Expense e = event.getRowValue();
            e.setAmount(event.getNewValue());
            db.writeDB();
            refreshExpenseTable();
        });

        // Категория (String)
        exp_categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        exp_categoryColumn.setOnEditCommit(event -> {
            Expense e = event.getRowValue();
            e.setCategory(event.getNewValue());
            db.writeDB();
            refreshExpenseTable();
        });
    }

    // Общий метод переключения режима редактирования
    private void setEditMode(boolean enable) {
        depositTable.setEditable(enable);
        creditTable.setEditable(enable);
        incomeTable.setEditable(enable);
        expenseTable.setEditable(enable);
        if (enable) {
            editModeToggleButton.setText("Редактирование: ВКЛ");
        } else {
            editModeToggleButton.setText("Редактирование: ВЫКЛ");
        }
    }
    //endregion

}