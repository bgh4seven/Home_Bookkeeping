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

    private Deposit createdDeposit;

    @FXML
    public void initialize() {
        // Предлагает сегодняшнюю дату по умолчанию
        datePicker.setValue(LocalDate.now());

        // НЕДОДЕЛАНО Действие на кнопку Cancel
        cancelButton.setOnAction(event -> {
            closeDialog();
        });
    }

    // Действие на кнопку ок
    @FXML
    private void okButton() {
        try {
            String bank = bankField.getText();
            double amount = Double.parseDouble(amountField.getText());
            double percent = Double.parseDouble(percentField.getText());
            int days = Integer.parseInt(daysField.getText());
            LocalDate openDate = datePicker.getValue();

            // MainController потом переназначает номер
            createdDeposit = new Deposit(0, bank, amount, percent, days, openDate);
            closeDialog();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Проверьте правильность чисел (сумма, процент, срок)");
            alert.showAndWait();
        }
    }

    // Закрывает окно
    @FXML
    private void closeDialog() {
        okButton.getScene().getWindow().hide();
    }

    // MainController вызывает для получения созданного вклада
    public Deposit getCreatedDeposit() {
        return createdDeposit;
    }
}
