package javafxtesting.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.models.Guest;

import java.io.IOException;

/**
 * Controller for Guest Registration View
 */
public class GuestRegistrationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button backButton;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        messageLabel.setText("");
    }

    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        // Get input values
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
            username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }

        try {
            // Check if username already exists
            if (Guest.findUserByUsername(username) != null) {
                showError("Username already exists. Please choose another.");
                return;
            }

            // Create new guest
            Guest newGuest = new Guest(username, password, name, email, phone);
            newGuest.saveUser();

            // Show success message
            showSuccess("Registration successful! You can now login.");

            // Wait a moment then redirect to login
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            handleBackToLogin(event);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (DatabaseException e) {
            showError("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle clear button click
     */
    @FXML
    private void handleClear(ActionEvent event) {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        messageLabel.setText("");
        nameField.requestFocus();
    }

    /**
     * Handle back to login button click
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxtesting/views/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 400, 500);
            stage.setTitle("Hotel Reservation System - Login");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showError("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }
}
