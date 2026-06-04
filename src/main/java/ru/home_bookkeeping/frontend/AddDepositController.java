package ru.home_bookkeeping.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.home_bookkeeping.backend.model.Deposit;

import java.time.LocalDate;

public class AddDepositController {

    @FXML
    private TextField bankField;
    @FXML private TextField amountField;
    @FXML private TextField percentField;
    @FXML private TextField daysField;
    @FXML private DatePicker datePicker;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private Deposit createdDeposit; // сюда сохраним результат

    @FXML
    public void initialize() {
        // Установи сегодняшнюю дату по умолчанию
        datePicker.setValue(LocalDate.now());

        // Действие на кнопку Cancel
        cancelButton.setOnAction(event -> {
            // Закрыть окно (нужно получить доступ к Stage, пока сделаем позже)
            closeDialog();
        });
    }

    // Метод, который будет вызываться при нажатии OK (привяжи в FXML)
    @FXML
    private void okButton() {
        try {
            String bank = bankField.getText();
            double amount = Double.parseDouble(amountField.getText());
            double percent = Double.parseDouble(percentField.getText());
            int days = Integer.parseInt(daysField.getText());
            LocalDate openDate = datePicker.getValue();

            // Временно номер = 0, потом MainController сам назначит номер
            createdDeposit = new Deposit(0, bank, amount, percent, days, openDate);
            closeDialog();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Проверьте правильность чисел (сумма, процент, срок)");
            alert.showAndWait();
        }
    }

    // Закрывает окно, в котором находится этот контроллер
    @FXML
    private void closeDialog() {
        okButton.getScene().getWindow().hide();
    }

    // Этот метод вызовет главный контроллер, чтобы забрать созданный вклад
    public Deposit getCreatedDeposit() {
        return createdDeposit;
    }
}
