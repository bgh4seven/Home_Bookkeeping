import backend.Deposit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import backend.BackendStub;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainController {

    // Сама таблица (тип Deposit — это класс-модель, который вы создадите позже)
    @FXML
    private TableView<Deposit> depositsTable;

    // Столбцы таблицы (имена должны совпадать с fx:id, заданными в Scene Builder)
    @FXML
    private TableColumn<Deposit, String> bankColumn;          // Название банка
    @FXML
    private TableColumn<Deposit, Double> percentColumn;       // Процент (например, 8.5)
    @FXML
    private TableColumn<Deposit, Integer> srokColumn;         // Срок вклада (в месяцах или днях)
    @FXML
    private TableColumn<Deposit, LocalDate> startDateColumn;  // Дата открытия
    @FXML
    private TableColumn<Deposit, LocalDate> endDateColumn;    // Дата погашения
    @FXML
    private TableColumn<Deposit, Double> expectedIncomeColumn; // Ожидаемая доходность

    @FXML
    private Button addDepositButton;    // Кнопка "Добавить вклад"
    @FXML
    private Button deleteDepositButton; // Кнопка "Удалить вклад"

    private BackendStub backend;

    @FXML
    public void initialize() {
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        srokColumn.setCellValueFactory(new PropertyValueFactory<>("termMonths"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        expectedIncomeColumn.setCellValueFactory(new PropertyValueFactory<>("expectedIncome"));

        addDepositButton.setOnAction(event -> {
            System.out.println("Добавить вклад — заглушка");
        });
        deleteDepositButton.setOnAction(event -> {
            System.out.println("Удалить вклад — заглушка");
        });
    }


}