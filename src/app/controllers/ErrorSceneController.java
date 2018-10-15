package app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * An ErrorController holds the responsibility of receiving input events
 * from the user while the error window is open. It then translates them
 * into actions on the views.
 */
public class ErrorSceneController {
    @FXML private Label _errorMessage;

    /**
     * When the okay button is pressed the error window is closed and the user
     * is displayed the search menu again.
     * @param event
     */
    public void okButtonAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Given a message string, when the error window is loaded in it will display
     * this message to the user informing them of the error they have made.
     * @param message
     */
    public void setMessage(String message) {
        _errorMessage.setText(message);
    }
}
