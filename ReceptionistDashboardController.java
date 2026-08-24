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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.models.Administrator;
import javafxtesting.models.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Controller for Manage Rooms View
 */
public class ManageRoomsController {

    @FXML
    private TableView<Room> roomsTable;

    @FXML
    private TableColumn<Room, Integer> roomIdColumn;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> roomTypeColumn;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableColumn<Room, Integer> capacityColumn;

    @FXML
    private TableColumn<Room, String> statusColumn;

    @FXML
    private Label messageLabel;

    private Administrator currentAdmin;
    private ObservableList<Room> roomsList;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup table columns
        roomIdColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getRoomId()).asObject());

        roomNumberColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoomNumber()));

        roomTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getRoomType()));

        priceColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());

        capacityColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());

        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus()));

        messageLabel.setText("");
    }

    /**
     * Set current administrator
     */
    public void setCurrentAdmin(Administrator admin) {
        this.currentAdmin = admin;
        loadRooms();
    }

    /**
     * Load rooms from database
     */
    private void loadRooms() {
        try {
            ArrayList<Room> rooms = currentAdmin.viewAllRooms();
            roomsList = FXCollections.observableArrayList(rooms);
            roomsTable.setItems(roomsList);

            messageLabel.setText("Showing " + rooms.size() + " rooms");
            messageLabel.setStyle("-fx-text-fill: green;");

        } catch (DatabaseException e) {
            showError("Failed to load rooms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle add room button
     */
    @FXML
    private void handleAddRoom(ActionEvent event) {
        // Get room number
        TextInputDialog roomNumberDialog = new TextInputDialog();
        roomNumberDialog.setTitle("Add Room");
        roomNumberDialog.setHeaderText("Enter Room Details");
        roomNumberDialog.setContentText("Room Number:");

        Optional<String> roomNumberResult = roomNumberDialog.showAndWait();
        if (!roomNumberResult.isPresent() || roomNumberResult.get().trim().isEmpty()) {
            return;
        }
        String roomNumber = roomNumberResult.get().trim();

        // Get room type
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Single",
            Arrays.asList("Single", "Double", "Suite"));
        typeDialog.setTitle("Add Room");
        typeDialog.setHeaderText("Enter Room Details");
        typeDialog.setContentText("Room Type:");

        Optional<String> typeResult = typeDialog.showAndWait();
        if (!typeResult.isPresent()) {
            return;
        }
        String roomType = typeResult.get();

        // Get price
        TextInputDialog priceDialog = new TextInputDialog();
        priceDialog.setTitle("Add Room");
        priceDialog.setHeaderText("Enter Room Details");
        priceDialog.setContentText("Price per Night:");

        Optional<String> priceResult = priceDialog.showAndWait();
        if (!priceResult.isPresent() || priceResult.get().trim().isEmpty()) {
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceResult.get().trim());
        } catch (NumberFormatException e) {
            showError("Invalid price format");
            return;
        }

        // Get capacity
        TextInputDialog capacityDialog = new TextInputDialog();
        capacityDialog.setTitle("Add Room");
        capacityDialog.setHeaderText("Enter Room Details");
        capacityDialog.setContentText("Capacity:");

        Optional<String> capacityResult = capacityDialog.showAndWait();
        if (!capacityResult.isPresent() || capacityResult.get().trim().isEmpty()) {
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityResult.get().trim());
        } catch (NumberFormatException e) {
            showError("Invalid capacity format");
            return;
        }

        // Get status
        ChoiceDialog<String> statusDialog = new ChoiceDialog<>("Available",
            Arrays.asList("Available", "Occupied", "Maintenance"));
        statusDialog.setTitle("Add Room");
        statusDialog.setHeaderText("Enter Room Details");
        statusDialog.setContentText("Status:");

        Optional<String> statusResult = statusDialog.showAndWait();
        if (!statusResult.isPresent()) {
            return;
        }
        String status = statusResult.get();

        try {
            Room newRoom = new Room(roomNumber, roomType, price, capacity, status);
            currentAdmin.addRoom(newRoom);

            showAlert(AlertType.INFORMATION, "Success", "Room added successfully");
            loadRooms();

        } catch (DatabaseException e) {
            showError("Failed to add room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle edit room button
     */
    @FXML
    private void handleEditRoom(ActionEvent event) {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();

        if (selectedRoom == null) {
            showError("Please select a room to edit");
            return;
        }

        // Get new status
        ChoiceDialog<String> statusDialog = new ChoiceDialog<>(selectedRoom.getStatus(),
            Arrays.asList("Available", "Occupied", "Maintenance"));
        statusDialog.setTitle("Edit Room");
        statusDialog.setHeaderText("Edit Room Status");
        statusDialog.setContentText("New Status:");

        Optional<String> statusResult = statusDialog.showAndWait();
        if (!statusResult.isPresent()) {
            return;
        }

        try {
            selectedRoom.setStatus(statusResult.get());
            currentAdmin.updateRoom(selectedRoom);

            showAlert(AlertType.INFORMATION, "Success", "Room updated successfully");
            loadRooms();

        } catch (DatabaseException e) {
            showError("Failed to update room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle remove room button
     */
    @FXML
    private void handleRemoveRoom(ActionEvent event) {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();

        if (selectedRoom == null) {
            showError("Please select a room to remove");
            return;
        }

        // Confirm removal
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove Room");
        confirmAlert.setContentText("Are you sure you want to remove room: " + selectedRoom.getRoomNumber() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentAdmin.removeRoom(selectedRoom.getRoomId());
                showAlert(AlertType.INFORMATION, "Success", "Room removed successfully");
                loadRooms();

            } catch (DatabaseException e) {
                showError("Failed to remove room: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle refresh button
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadRooms();
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
