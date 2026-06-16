package ru.home_bookkeeping.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
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
    @FXML private Button addCreditButton;
    @FXML private Button deleteCreditButton;

    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, Integer> inc_numberColumn;
    @FXML private TableColumn<Income, Double>  inc_amountColumn;
    @FXML private TableColumn<Income, String>  inc_sourceColumn;   // источник дохода
    @FXML private Button addIncomeButton;
    @FXML private Button deleteIncomeButton;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private BarChart<Number, String> incomeChart;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, Integer> exp_numberColumn;
    @FXML private TableColumn<Expense, Double>  exp_amountColumn;
    @FXML private TableColumn<Expense, String>  exp_categoryColumn; // категория расхода
    @FXML private Button addExpenseButton;
    @FXML private Button deleteExpenseButton;
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
        dep_srokColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
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

        addCreditButton.setOnAction(event -> {
            showAddCreditDialog();
            //refreshCreditTable();
        });
        deleteCreditButton.setOnAction(event -> {
            deleteSelectedCredit();
            //refreshCreditTable();
        });

        refreshCreditTable();
    }
    private void refreshCreditTable() {
        creditTable.getItems().clear();
        creditTable.getItems().addAll(db.getAllCredits());
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
        yearComboBox.getItems().addAll(2023, 2024, 2025, 2026); // можно динамически вычислять
        yearComboBox.setValue(LocalDate.now().getYear());
        monthComboBox.getItems().addAll("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь");
        monthComboBox.setValue(monthName(LocalDate.now().getMonthValue()));

        // Слушатели
        yearComboBox.setOnAction(e -> updateIncomeChart());
        monthComboBox.setOnAction(e -> updateIncomeChart());

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
        int year = yearComboBox.getValue();
        String monthName = monthComboBox.getValue();
        int month = monthNameToNumber(monthName);

        List<Income> all = db.getAllIncomes();
        Map<String, Double> sourceSum = new HashMap<>();

        for (Income inc : all) {
            LocalDate d = inc.getDate();
            if (d != null && d.getYear() == year && d.getMonthValue() == month) {
                String src = inc.getSource();
                sourceSum.put(src, sourceSum.getOrDefault(src, 0.0) + inc.getAmount());
            }
        }

        // Очищаем старые данные
        incomeChart.getData().clear();

        // Для каждой категории создаём отдельную серию
        for (Map.Entry<String, Double> entry : sourceSum.entrySet()) {
            String category = entry.getKey();
            double sum = entry.getValue();
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName(category);
            // ПРАВИЛЬНЫЙ порядок: число (X) идёт первым, категория (Y) – вторым
            series.getData().add(new XYChart.Data<>(sum, category));
            incomeChart.getData().add(series);
        }

        // Если данных нет – показываем заглушку
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

        refreshExpenseTable();
    }
    private void refreshExpenseTable() {
        expenseTable.getItems().clear();
        expenseTable.getItems().addAll(db.getAllExpenses());
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