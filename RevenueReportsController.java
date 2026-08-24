package javafxtesting.controllers;

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
import javafx.scene.control.TextInputDialog;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.models.Administrator;
import javafxtesting.models.Receptionist;
import javafxtesting.models.User;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller for Manage Users View
 */
public class ManageUsersController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, String> userTypeColumn;

    @FXML
    private Label messageLabel;

    private Administrator currentAdmin;
    private ObservableList<User> usersList;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Setup table columns
        userIdColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());

        usernameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getUsername()));

        nameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getName()));

        emailColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getEmail()));

        phoneColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getPhone()));

        userTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getUserType()));

        messageLabel.setText("");
    }

    /**
     * Set current administrator
     */
    public void setCurrentAdmin(Administrator admin) {
        this.currentAdmin = admin;
        loadUsers();
    }

    /**
     * Load users from database
     */
    private void loadUsers() {
        try {
            ArrayList<User> users = currentAdmin.viewAllUsers();
            usersList = FXCollections.observableArrayList(users);
            usersTable.setItems(usersList);

            messageLabel.setText("Showing " + users.size() + " users");
            messageLabel.setStyle("-fx-text-fill: green;");

        } catch (DatabaseException e) {
            showError("Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle add receptionist button
     */
    @FXML
    private void handleAddReceptionist(ActionEvent event) {
        // Create a simple dialog to get receptionist details
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Receptionist");
        nameDialog.setHeaderText("Enter Receptionist Details");
        nameDialog.setContentText("Full Name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (!nameResult.isPresent() || nameResult.get().trim().isEmpty()) {
            return;
        }
        String name = nameResult.get().trim();

        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Add Receptionist");
        usernameDialog.setHeaderText("Enter Receptionist Details");
        usernameDialog.setContentText("Username:");

        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (!usernameResult.isPresent() || usernameResult.get().trim().isEmpty()) {
            return;
        }
        String username = usernameResult.get().trim();

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Add Receptionist");
        passwordDialog.setHeaderText("Enter Receptionist Details");
        passwordDialog.setContentText("Password:");

        Optional<String> passwordResult = passwordDialog.showAndWait();
        if (!passwordResult.isPresent() || passwordResult.get().trim().isEmpty()) {
            return;
        }
        String password = passwordResult.get().trim();

        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Add Receptionist");
        emailDialog.setHeaderText("Enter Receptionist Details");
        emailDialog.setContentText("Email:");

        Optional<String> emailResult = emailDialog.showAndWait();
        if (!emailResult.isPresent() || emailResult.get().trim().isEmpty()) {
            return;
        }
        String email = emailResult.get().trim();

        TextInputDialog phoneDialog = new TextInputDialog();
        phoneDialog.setTitle("Add Receptionist");
        phoneDialog.setHeaderText("Enter Receptionist Details");
        phoneDialog.setContentText("Phone:");

        Optional<String> phoneResult = phoneDialog.showAndWait();
        if (!phoneResult.isPresent() || phoneResult.get().trim().isEmpty()) {
            return;
        }
        String phone = phoneResult.get().trim();

        try {
            // Check if username exists
            if (User.findUserByUsername(username) != null) {
                showError("Username already exists");
                return;
            }

            // Create new receptionist
            Receptionist newReceptionist = new Receptionist(username, password, name, email, phone);
            currentAdmin.addReceptionist(newReceptionist);

            showAlert(AlertType.INFORMATION, "Success", "Receptionist added successfully");
            loadUsers();

        } catch (DatabaseException e) {
            showError("Failed to add receptionist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle remove user button
     */
    @FXML
    private void handleRemoveUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError("Please select a user to remove");
            return;
        }

        // Don't allow removing administrators
        if ("Administrator".equals(selectedUser.getUserType())) {
            showError("Cannot remove administrators");
            return;
        }

        // Confirm removal
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove User");
        confirmAlert.setContentText("Are you sure you want to remove user: " + selectedUser.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentAdmin.removeUser(selectedUser.getUserId());
                showAlert(AlertType.INFORMATION, "Success", "User removed successfully");
                loadUsers();

            } catch (DatabaseException e) {
                showError("Failed to remove user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle refresh button
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
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
