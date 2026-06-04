package ru.home_bookkeeping.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import ru.home_bookkeeping.backend.DataBase;
import ru.home_bookkeeping.backend.model.Deposit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

//Объект данного класса создается в fxml, в Main достаточно обозначить загрузку fxml
public class MainController {
    private DataBase db = new DataBase();


    // Сама таблица
    @FXML
    private TableView<Deposit> depositTable;


    // Столбцы таблицы (имена должны совпадать с fx:id, заданными в Scene Builder)
    @FXML
    private TableColumn<Deposit, Integer> numberColumn;
    @FXML
    private TableColumn<Deposit, String> bankColumn;          // Название банка
    @FXML
    private TableColumn<Deposit, Double> amountColumn;
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


    /**
     * Этот метод вызывается автоматически после загрузки fxml.
     * setCellValueFactory — настраивает, откуда столбец берёт значения для каждой строки.
     * new PropertyValueFactory<>("bankName") — у объекта-поставщика вызывает метод getBankName() и берет значение для ячейки (геттер ищется автоматически)
     */
    @FXML
    public void initialize() {
        numberColumn.setCellValueFactory((new PropertyValueFactory<>("number")));
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        srokColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("openDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("closeDate"));
        expectedIncomeColumn.setCellValueFactory(new PropertyValueFactory<>("income"));

        addDepositButton.setOnAction(event -> {
            System.out.println("Adding deposit");
            try {
                showAddDepositDialog();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Deposit added");
            refreshTable();
        });
        deleteDepositButton.setOnAction(event -> {
            System.out.println("Deleting deposit");
            db.deleteDeposit(1);
            refreshTable();
        });
        refreshTable();
        enableEditMode();
    }

    private void refreshTable() {
        depositTable.getItems().clear();                 // очищаем таблицу
        List<Deposit> allDeposits = db.getAllDeposits(); // получаем вклады из БД
        depositTable.getItems().addAll(allDeposits);     // добавляем все в таблицу
    }

    //Позже добавлю редактирование ячеек
    private void enableEditMode() {
        depositTable.setEditable(true);
        bankColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        bankColumn.setOnEditCommit(event -> {
            Deposit deposit = event.getRowValue();
            deposit.setBankName(event.getNewValue());
            db.writeDB(); // сохранить изменения
        });
    }
    private void disableEtitMode() {
        depositTable.setEditable(false);
    }

    private void showAddDepositDialog() throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-deposit-view.fxml"));
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
                int newNumber = db.getNextNumber();
                // Создаём вклад с правильным номером
                db.addDeposit(newNumber, newDeposit.getBankName(), newDeposit.getAmount(),
                        newDeposit.getPercent(), newDeposit.getDays(), newDeposit.getOpenDate());
                db.writeDB();
                refreshTable();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Не удалось загрузить окно добавления вклада");
            alert.showAndWait();
        }
    }
}