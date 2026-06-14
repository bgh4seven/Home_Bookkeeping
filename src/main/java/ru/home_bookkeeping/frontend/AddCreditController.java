package ru.home_bookkeeping.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.home_bookkeeping.backend.model.Credit;
import java.time.LocalDate;

public class AddCreditController {

    @FXML private TextField bankField;
    @FXML private TextField amountField;
    @FXML private TextField percentField;
    @FXML private TextField monthlyPaymentField;
    @FXML private TextField monthsField;
    @FXML private DatePicker openDatePicker;
    @FXML private Button cancelButton;
    @FXML private Button okButton;

    private Credit createdCredit;

    @FXML
    public void initialize() {
        openDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleOk() {
        try {
            String bank = bankField.getText();
            double amount = Double.parseDouble(amountField.getText());
            double percent = Double.parseDouble(percentField.getText());
            double monthlyPayment = Double.parseDouble(monthlyPaymentField.getText());
            int months = Integer.parseInt(monthsField.getText());
            LocalDate openDate = openDatePicker.getValue();

            createdCredit = new Credit(0, bank, amount, percent, monthlyPayment, months, openDate);
            closeDialog();
        } catch (NumberFormatException e) {
            showError("Проверьте ввод чисел (сумма, процент, платёж, срок)");
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        okButton.getScene().getWindow().hide();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public Credit getCreatedCredit() {
        return createdCredit;
    }
}
