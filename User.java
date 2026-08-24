package javafxtesting;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafxtesting.database.DatabaseConnection;

/**
 * Main Application Class
 * Entry point for the Hotel Reservation System
 * CS313 Project B - Hotel Reservation System
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            System.out.println("Testing database connection...");
            if (DatabaseConnection.testConnection()) {
                System.out.println("Database connection successful!");
            } else {
                System.err.println("Database connection failed!");
                showErrorDialog("Database Connection Error",
                              "Failed to connect to the database.\n" +
                              "Please ensure Oracle Database is running and credentials are correct.\n" +
                              "Username: system\nPassword: 1234");
                return;
            }

            // Load Login View
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxtesting/views/LoginView.fxml"));
            Parent root = loader.load();

            // Setup Scene and Stage
            Scene scene = new Scene(root, 400, 500);
            primaryStage.setTitle("Hotel Reservation System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Show stage
            primaryStage.show();

            System.out.println("Application started successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Application Error",
                          "Failed to start the application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        // Close database connection when application closes
        DatabaseConnection.closeConnection();
        System.out.println("Application stopped. Database connection closed.");
    }

    /**
     * Show error dialog
     */
    private void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Hotel Reservation System");
        System.out.println("  CS313 Advanced Programming Language");
        System.out.println("  Term 1, 2025-2026");
        System.out.println("========================================");

        launch(args);
    }
}
