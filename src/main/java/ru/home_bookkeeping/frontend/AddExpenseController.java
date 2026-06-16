package ru.home_bookkeeping.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.home_bookkeeping.backend.model.Expense;

import java.time.LocalDate;
import java.util.HashSet;

public class AddExpenseController {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button cancelButton;
    @FXML private Button okButton;

    private Expense createdExpense;

    @FXML
    public void initialize() {
        HashSet<String> categories = Expense.getCategories();
        categoryComboBox.getItems().addAll(categories);
        if (!categoryComboBox.getItems().isEmpty())
            categoryComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleOk() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryComboBox.getValue();
            if (category == null || category.isEmpty()) {
                showError("Выберите категорию расхода");
                return;
            }
            createdExpense = new Expense(0, amount, category, LocalDate.now());
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

    public Expense getCreatedExpense() {
        return createdExpense;
    }
}
