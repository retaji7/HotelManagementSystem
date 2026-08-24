package javafxtesting.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafxtesting.models.Guest;

import java.io.IOException;

/**
 * Controller for Guest Dashboard
 */
public class GuestDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane mainBorderPane;

    private Guest currentGuest;

    /**
     * Set current guest
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
        welcomeLabel.setText("Welcome, " + guest.getName());
    }

    /**
     * Load Browse Rooms view
     */
    @FXML
    private void handleBrowseRooms(ActionEvent event) {
        loadView("/javafxtesting/views/BrowseRoomsView.fxml");
    }

    /**
     * Load Make Reservation view
     */
    @FXML
    private void handleMakeReservation(ActionEvent event) {
        loadView("/javafxtesting/views/MakeReservationView.fxml");
    }

    /**
     * Load My Reservations view
     */
    @FXML
    private void handleMyReservations(ActionEvent event) {
        loadView("/javafxtesting/views/MyReservationsView.fxml");
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

            // Pass current guest to the loaded controller
            Object controller = loader.getController();
            if (controller instanceof BrowseRoomsController) {
                ((BrowseRoomsController) controller).setCurrentGuest(currentGuest);
            } else if (controller instanceof MakeReservationController) {
                ((MakeReservationController) controller).setCurrentGuest(currentGuest);
            } else if (controller instanceof MyReservationsController) {
                ((MyReservationsController) controller).setCurrentGuest(currentGuest);
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

    public Guest getCurrentGuest() {
        return currentGuest;
    }
}
