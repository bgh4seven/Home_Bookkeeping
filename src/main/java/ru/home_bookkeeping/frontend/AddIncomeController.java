package ru.home_bookkeeping.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.home_bookkeeping.backend.model.Income;

import java.time.LocalDate;
import java.util.HashSet;

public class AddIncomeController {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> sourceComboBox;
    @FXML private Button cancelButton;
    @FXML private Button okButton;

    private Income createdIncome;

    @FXML
    public void initialize() {
        HashSet<String> sources = Income.getSources();
        sourceComboBox.getItems().addAll(sources);
        if (!sourceComboBox.getItems().isEmpty())
            sourceComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleOk() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String source = sourceComboBox.getValue();

            if (source == null || source.isEmpty()) {
                showError("Выберите источник дохода");
                return;
            }
            createdIncome = new Income(0, amount, source, LocalDate.now());
            closeDialog();
        } catch (NumberFormatException e) {
            showError("Введите корректную сумму");
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

    public Income getCreatedIncome() {
        return createdIncome;
    }
}
