package javafxtesting.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.models.Administrator;

import java.time.LocalDate;

/**
 * Controller for Revenue Reports View
 */
public class RevenueReportsController {

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Label messageLabel;

    private Administrator currentAdmin;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup report type combo box
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
            "Total Revenue",
            "Revenue by Room Type",
            "Revenue by Date Range"
        ));

        reportTextArea.setText("Select a report type and click 'Generate Report'");
        messageLabel.setText("");
    }

    /**
     * Set current administrator
     */
    public void setCurrentAdmin(Administrator admin) {
        this.currentAdmin = admin;
    }

    /**
     * Handle generate report button
     */
    @FXML
    private void handleGenerateReport(ActionEvent event) {
        String reportType = reportTypeComboBox.getValue();

        if (reportType == null || reportType.isEmpty()) {
            showError("Please select a report type");
            return;
        }

        try {
            String reportContent = "";

            switch (reportType) {
                case "Total Revenue":
                    reportContent = currentAdmin.generateTotalRevenueReport();
                    break;

                case "Revenue by Room Type":
                    reportContent = currentAdmin.generateRevenueByRoomTypeReport();
                    break;

                case "Revenue by Date Range":
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();

                    if (startDate == null || endDate == null) {
                        showError("Please select both start and end dates for date range report");
                        return;
                    }

                    if (startDate.isAfter(endDate)) {
                        showError("Start date must be before end date");
                        return;
                    }

                    reportContent = currentAdmin.generateRevenueByDateRangeReport(
                        startDate.toString(),
                        endDate.toString()
                    );
                    break;

                default:
                    showError("Unknown report type");
                    return;
            }

            reportTextArea.setText(reportContent);
            showSuccess("Report generated successfully");

        } catch (DatabaseException e) {
            showError("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle clear button
     */
    @FXML
    private void handleClear(ActionEvent event) {
        reportTypeComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reportTextArea.setText("Select a report type and click 'Generate Report'");
        messageLabel.setText("");
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }
}
