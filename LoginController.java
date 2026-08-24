package javafxtesting.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.models.Guest;
import javafxtesting.models.Room;

import java.util.ArrayList;

/**
 * Controller for Browse Rooms View
 */
public class BrowseRoomsController {

    @FXML
    private ComboBox<String> roomTypeComboBox;

    @FXML
    private TableView<Room> roomsTable;

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

    private Guest currentGuest;
    private ObservableList<Room> roomsList;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup room type combo box
        roomTypeComboBox.setItems(FXCollections.observableArrayList("All", "Single", "Double", "Suite"));
        roomTypeComboBox.setValue("All");

        // Load initial data
        loadRooms();
    }

    /**
     * Set current guest
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
    }

    /**
     * Load rooms from database
     */
    private void loadRooms() {
        try {
            ArrayList<Room> rooms = Room.getAvailableRooms();
            roomsList = FXCollections.observableArrayList(rooms);
            roomsTable.setItems(roomsList);
            messageLabel.setText("Showing " + rooms.size() + " available rooms");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (DatabaseException e) {
            showError("Failed to load rooms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle apply filter button
     */
    @FXML
    private void handleApplyFilter(ActionEvent event) {
        String selectedType = roomTypeComboBox.getValue();

        if (selectedType == null || "All".equals(selectedType)) {
            loadRooms();
            return;
        }

        try {
            ArrayList<Room> rooms = Room.getRoomsByType(selectedType);
            // Filter to show only available rooms
            ArrayList<Room> availableRooms = new ArrayList<>();
            for (Room room : rooms) {
                if (room.isAvailable()) {
                    availableRooms.add(room);
                }
            }

            roomsList = FXCollections.observableArrayList(availableRooms);
            roomsTable.setItems(roomsList);
            messageLabel.setText("Showing " + availableRooms.size() + " available " + selectedType + " rooms");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (DatabaseException e) {
            showError("Failed to filter rooms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle show all button
     */
    @FXML
    private void handleShowAll(ActionEvent event) {
        roomTypeComboBox.setValue("All");
        loadRooms();
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
