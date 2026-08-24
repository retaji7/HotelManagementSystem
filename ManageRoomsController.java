package javafxtesting.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafxtesting.exceptions.DatabaseException;
import javafxtesting.exceptions.LoginException;
import javafxtesting.models.User;
import javafxtesting.models.Guest;
import javafxtesting.models.Receptionist;
import javafxtesting.models.Administrator;

import java.io.IOException;

/**
 * Controller for Login View
 * Handles user authentication and navigation to appropriate dashboards
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button clearButton;

    @FXML
    private Hyperlink registerLink;

    // Currently logged in user
    private static User currentUser;

    /**
     * Initialize method (called automatically after FXML load)
     */
    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            // Find user by username
            User user = User.findUserByUsername(username);

            if (user == null) {
                showError("User not found");
                return;
            }

            // Authenticate
            if (user.authenticate(username, password)) {
                currentUser = user;
                errorLabel.setText("");

                // Navigate to appropriate dashboard based on user type
                navigateToDashboard(event, user);
            }

        } catch (DatabaseException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (LoginException e) {
            showError("Login failed: " + e.getMessage());
        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to appropriate dashboard based on user type
     */
    private void navigateToDashboard(ActionEvent event, User user) throws IOException {
        FXMLLoader loader;
        String dashboardPath;

        // Determine which dashboard to load based on user type (Polymorphism)
        if (user instanceof Guest) {
            dashboardPath = "/javafxtesting/views/GuestDashboardView.fxml";
            loader = new FXMLLoader(getClass().getResource(dashboardPath));
            Parent root = loader.load();

            // Pass current user to dashboard controller
            GuestDashboardController controller = loader.getController();
            controller.setCurrentGuest((Guest) user);

            showDashboard(event, root, "Guest Dashboard");

        } else if (user instanceof Receptionist) {
            dashboardPath = "/javafxtesting/views/ReceptionistDashboardView.fxml";
            loader = new FXMLLoader(getClass().getResource(dashboardPath));
            Parent root = loader.load();

            // Pass current user to dashboard controller
            ReceptionistDashboardController controller = loader.getController();
            controller.setCurrentReceptionist((Receptionist) user);

            showDashboard(event, root, "Receptionist Dashboard");

        } else if (user instanceof Administrator) {
            dashboardPath = "/javafxtesting/views/AdminDashboardView.fxml";
            loader = new FXMLLoader(getClass().getResource(dashboardPath));
            Parent root = loader.load();

            // Pass current user to dashboard controller
            AdminDashboardController controller = loader.getController();
            controller.setCurrentAdmin((Administrator) user);

            showDashboard(event, root, "Administrator Dashboard");
        }
    }

    /**
     * Show dashboard window
     */
    private void showDashboard(ActionEvent event, Parent root, String title) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handle clear button click
     */
    @FXML
    private void handleClear(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setText("");
        usernameField.requestFocus();
    }

    /**
     * Handle register link click
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxtesting/views/GuestRegistrationView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 600, 500);
            stage.setTitle("Guest Registration");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showError("Failed to load registration page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
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

    /**
     * Get current logged in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set current user (used after registration)
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Logout current user
     */
    public static void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
    }
}
