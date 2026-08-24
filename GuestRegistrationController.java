package javafxtesting.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafxtesting.models.Administrator;

import java.io.IOException;

/**
 * Controller for Administrator Dashboard
 */
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane mainBorderPane;

    private Administrator currentAdmin;

    /**
     * Set current administrator
     */
    public void setCurrentAdmin(Administrator admin) {
        this.currentAdmin = admin;
        welcomeLabel.setText("Welcome, " + admin.getName());
    }

    /**
     * Load Manage Users view
     */
    @FXML
    private void handleManageUsers(ActionEvent event) {
        loadView("/javafxtesting/views/ManageUsersView.fxml");
    }

    /**
     * Load Manage Rooms view
     */
    @FXML
    private void handleManageRooms(ActionEvent event) {
        loadView("/javafxtesting/views/ManageRoomsView.fxml");
    }

    /**
     * Load Revenue Reports view
     */
    @FXML
    private void handleRevenueReports(ActionEvent event) {
        loadView("/javafxtesting/views/RevenueReportsView.fxml");
    }

    /**
     * Handle logout
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        LoginController.logout();
        loadLoginView(event);
    }

    /**
     * Load view into center pane
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Pass current admin to the loaded controller
            Object controller = loader.getController();
            if (controller instanceof ManageUsersController) {
                ((ManageUsersController) controller).setCurrentAdmin(currentAdmin);
            } else if (controller instanceof ManageRoomsController) {
                ((ManageRoomsController) controller).setCurrentAdmin(currentAdmin);
            } else if (controller instanceof RevenueReportsController) {
                ((RevenueReportsController) controller).setCurrentAdmin(currentAdmin);
            }

            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load login view
     */
    private void loadLoginView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxtesting/views/LoginView.fxml"));
            Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 500);
            stage.setTitle("Hotel Reservation System - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Administrator getCurrentAdmin() {
        return currentAdmin;
    }
}
