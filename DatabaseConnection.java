package javafxtesting.controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.exceptions.InvalidReservationException;
import javafxtesting.models.Guest;
import javafxtesting.models.Reservation;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller for My Reservations View
 */
public class MyReservationsController {

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, Integer> reservationIdColumn;

    @FXML
    private TableColumn<Reservation, String> roomNumberColumn;

    @FXML
    private TableColumn<Reservation, String> roomTypeColumn;

    @FXML
    private TableColumn<Reservation, String> checkInColumn;

    @FXML
    private TableColumn<Reservation, String> checkOutColumn;

    @FXML
    private TableColumn<Reservation, Double> totalPriceColumn;

    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private Label messageLabel;

    private Guest currentGuest;
    private ObservableList<Reservation> reservationsList;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup table columns
        reservationIdColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getReservationId()).asObject());

        roomNumberColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoom().getRoomNumber()));

        roomTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoom().getRoomType()));

        checkInColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCheckInDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        checkOutColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCheckOutDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        totalPriceColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus()));

        messageLabel.setText("");
    }

    /**
     * Set current guest
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
        loadReservations();
    }

    /**
     * Load reservations from database
     */
    private void loadReservations() {
        if (currentGuest == null) {
            showError("Guest not set");
            return;
        }

        try {
            ArrayList<Reservation> reservations = currentGuest.viewMyReservations();
            reservationsList = FXCollections.observableArrayList(reservations);
            reservationsTable.setItems(reservationsList);

            messageLabel.setText("Showing " + reservations.size() + " reservations");
            messageLabel.setStyle("-fx-text-fill: green;");

        } catch (DatabaseException e) {
            showError("Failed to load reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle cancel reservation button
     */
    @FXML
    private void handleCancelReservation(ActionEvent event) {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            showError("Please select a reservation to cancel");
            return;
        }

        // Check if reservation can be cancelled
        String status = selectedReservation.getStatus();
        if ("Cancelled".equals(status)) {
            showError("This reservation is already cancelled");
            return;
        }

        if ("Checked-Out".equals(status)) {
            showError("Cannot cancel a completed reservation");
            return;
        }

        // Confirm cancellation
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Reservation");
        confirmAlert.setContentText("Are you sure you want to cancel this reservation?\n" +
                                   "Reservation ID: " + selectedReservation.getReservationId() + "\n" +
                                   "Room: " + selectedReservation.getRoom().getRoomNumber());

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentGuest.cancelReservation(selectedReservation.getReservationId());

                showAlert(AlertType.INFORMATION, "Success", "Reservation cancelled successfully");

                // Reload reservations
                loadReservations();

            } catch (DatabaseException e) {
                showError("Failed to cancel reservation: " + e.getMessage());
                e.printStackTrace();
            } catch (InvalidReservationException e) {
                showError("Cannot cancel reservation: " + e.getMessage());
            }
        }
    }

    /**
     * Handle refresh button
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadReservations();
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Show alert dialog
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
