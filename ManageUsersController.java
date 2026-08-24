package javafxtesting.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.exceptions.InvalidReservationException;
import javafxtesting.models.Guest;
import javafxtesting.models.Reservation;
import javafxtesting.models.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Controller for Make Reservation View
 */
public class MakeReservationController {

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private ComboBox<String> roomTypeComboBox;

    @FXML
    private TextField numberOfGuestsField;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label messageLabel;

    private Guest currentGuest;
    private double calculatedPrice = 0.0;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup room type combo box
        roomTypeComboBox.setItems(FXCollections.observableArrayList("Single", "Double", "Suite"));

        // Clear message
        messageLabel.setText("");
    }

    /**
     * Set current guest
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
    }

    /**
     * Handle calculate price button
     */
    @FXML
    private void handleCalculatePrice(ActionEvent event) {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        String roomType = roomTypeComboBox.getValue();

        // Validation
        if (checkIn == null || checkOut == null) {
            showError("Please select check-in and check-out dates");
            return;
        }

        if (checkIn.isAfter(checkOut)) {
            showError("Check-in date must be before check-out date");
            return;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            showError("Check-in date cannot be in the past");
            return;
        }

        if (roomType == null || roomType.isEmpty()) {
            showError("Please select a room type");
            return;
        }

        try {
            // Get available rooms of selected type
            ArrayList<Room> rooms = Room.getRoomsByType(roomType);

            if (rooms.isEmpty()) {
                showError("No rooms available of type: " + roomType);
                return;
            }

            // Get the first available room for price calculation
            Room sampleRoom = null;
            for (Room room : rooms) {
                if (room.isAvailable()) {
                    sampleRoom = room;
                    break;
                }
            }

            if (sampleRoom == null) {
                showError("No available rooms of type: " + roomType);
                return;
            }

            // Calculate total price
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            calculatedPrice = nights * sampleRoom.getPrice();

            totalPriceLabel.setText(String.format("$%.2f (%d nights x $%.2f)", calculatedPrice, nights, sampleRoom.getPrice()));
            showSuccess("Price calculated successfully");

        } catch (DatabaseException e) {
            showError("Failed to calculate price: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle submit button
     */
    @FXML
    private void handleSubmit(ActionEvent event) {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        String roomType = roomTypeComboBox.getValue();
        String numberOfGuestsStr = numberOfGuestsField.getText().trim();

        // Validation
        if (checkIn == null || checkOut == null) {
            showError("Please select check-in and check-out dates");
            return;
        }

        if (checkIn.isAfter(checkOut)) {
            showError("Check-in date must be before check-out date");
            return;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            showError("Check-in date cannot be in the past");
            return;
        }

        if (roomType == null || roomType.isEmpty()) {
            showError("Please select a room type");
            return;
        }

        if (numberOfGuestsStr.isEmpty()) {
            showError("Please enter number of guests");
            return;
        }

        int numberOfGuests;
        try {
            numberOfGuests = Integer.parseInt(numberOfGuestsStr);
            if (numberOfGuests <= 0) {
                showError("Number of guests must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid number of guests");
            return;
        }

        try {
            // Find an available room of the selected type
            ArrayList<Room> rooms = Room.getRoomsByType(roomType);
            Room selectedRoom = null;

            for (Room room : rooms) {
                if (room.isAvailable()) {
                    // Check capacity
                    if (numberOfGuests <= room.getCapacity()) {
                        selectedRoom = room;
                        break;
                    }
                }
            }

            if (selectedRoom == null) {
                showError("No available rooms of type " + roomType + " for " + numberOfGuests + " guests");
                return;
            }

            // Create reservation
            Reservation reservation = new Reservation(currentGuest, selectedRoom, checkIn, checkOut, "Pending");
            reservation.saveReservation();

            showAlert(AlertType.INFORMATION, "Success",
                     "Reservation created successfully!\n" +
                     "Reservation ID: " + reservation.getReservationId() + "\n" +
                     "Room: " + selectedRoom.getRoomNumber() + "\n" +
                     "Total Price: $" + String.format("%.2f", reservation.getTotalPrice()) + "\n" +
                     "Status: Pending");

            // Clear form
            handleClear(event);

        } catch (DatabaseException e) {
            showError("Failed to create reservation: " + e.getMessage());
            e.printStackTrace();
        } catch (InvalidReservationException e) {
            showError("Invalid reservation: " + e.getMessage());
        }
    }

    /**
     * Handle clear button
     */
    @FXML
    private void handleClear(ActionEvent event) {
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        roomTypeComboBox.setValue(null);
        numberOfGuestsField.clear();
        totalPriceLabel.setText("$0.00");
        messageLabel.setText("");
        calculatedPrice = 0.0;
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
