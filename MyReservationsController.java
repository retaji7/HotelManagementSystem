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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.exceptions.InvalidReservationException;
import javafxtesting.models.Receptionist;
import javafxtesting.models.Reservation;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller for Manage Reservations View
 */
public class ManageReservationsController {

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, Integer> reservationIdColumn;

    @FXML
    private TableColumn<Reservation, String> guestNameColumn;

    @FXML
    private TableColumn<Reservation, String> roomNumberColumn;

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

    private Receptionist currentReceptionist;
    private ObservableList<Reservation> reservationsList;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup table columns
        reservationIdColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getReservationId()).asObject());

        guestNameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getGuest().getName()));

        roomNumberColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoom().getRoomNumber()));

        checkInColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCheckInDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        checkOutColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCheckOutDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        totalPriceColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus()));

        // Setup status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
            "All", "Pending", "Confirmed", "Checked-In", "Checked-Out", "Cancelled"));
        statusComboBox.setValue("All");

        messageLabel.setText("");
    }

    /**
     * Set current receptionist
     */
    public void setCurrentReceptionist(Receptionist receptionist) {
        this.currentReceptionist = receptionist;
        loadReservations();
    }

    /**
     * Load all reservations
     */
    private void loadReservations() {
        try {
            ArrayList<Reservation> reservations = currentReceptionist.viewAllReservations();
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
     * Handle apply filter button
     */
    @FXML
    private void handleApplyFilter(ActionEvent event) {
        String selectedStatus = statusComboBox.getValue();

        if (selectedStatus == null || "All".equals(selectedStatus)) {
            loadReservations();
            return;
        }

        try {
            ArrayList<Reservation> reservations = currentReceptionist.viewReservationsByStatus(selectedStatus);
            reservationsList = FXCollections.observableArrayList(reservations);
            reservationsTable.setItems(reservationsList);

            messageLabel.setText("Showing " + reservations.size() + " " + selectedStatus + " reservations");
            messageLabel.setStyle("-fx-text-fill: green;");

        } catch (DatabaseException e) {
            showError("Failed to filter reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle show all button
     */
    @FXML
    private void handleShowAll(ActionEvent event) {
        statusComboBox.setValue("All");
        loadReservations();
    }

    /**
     * Handle confirm reservation button
     */
    @FXML
    private void handleConfirm(ActionEvent event) {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a reservation to confirm");
            return;
        }

        if (!"Pending".equals(selected.getStatus())) {
            showError("Only pending reservations can be confirmed");
            return;
        }

        try {
            currentReceptionist.confirmReservation(selected.getReservationId());
            showAlert(AlertType.INFORMATION, "Success", "Reservation confirmed successfully");
            loadReservations();

        } catch (DatabaseException | InvalidReservationException e) {
            showError("Failed to confirm reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle check-in button
     */
    @FXML
    private void handleCheckIn(ActionEvent event) {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a reservation to check-in");
            return;
        }

        if (!"Confirmed".equals(selected.getStatus())) {
            showError("Only confirmed reservations can be checked in");
            return;
        }

        // Confirm check-in
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Check-In");
        confirmAlert.setHeaderText("Check-In Guest");
        confirmAlert.setContentText("Check-in guest: " + selected.getGuest().getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentReceptionist.checkInGuest(selected.getReservationId());
                showAlert(AlertType.INFORMATION, "Success", "Guest checked in successfully");
                loadReservations();

            } catch (DatabaseException | InvalidReservationException e) {
                showError("Failed to check-in guest: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle check-out button
     */
    @FXML
    private void handleCheckOut(ActionEvent event) {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a reservation to check-out");
            return;
        }

        if (!"Checked-In".equals(selected.getStatus())) {
            showError("Only checked-in guests can be checked out");
            return;
        }

        // Confirm check-out
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Check-Out");
        confirmAlert.setHeaderText("Check-Out Guest");
        confirmAlert.setContentText("Check-out guest: " + selected.getGuest().getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentReceptionist.checkOutGuest(selected.getReservationId());
                showAlert(AlertType.INFORMATION, "Success", "Guest checked out successfully");
                loadReservations();

            } catch (DatabaseException | InvalidReservationException e) {
                showError("Failed to check-out guest: " + e.getMessage());
                e.printStackTrace();
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
