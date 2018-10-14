package app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * An ConfirmSceneController holds the responsibility of receiving input events
 * from the user while the confirm window is open. It is responsible for updating
 * the confirm message displayed to the user, and also translating the users
 * decision to actions on the views.
 */
public class ConfirmSceneController {
    @FXML private Label _confirmMessage;

    public void yesButtonAction(ActionEvent event) {
    }

    public void noButtonAction(ActionEvent event) {
    }

    /**
     * Given a message string, when the confirm window is loaded in it will display
     * this message to the user informing them of the decision they have to make.
     * @param message
     */
    public void setMessage(String message) {
        _confirmMessage.setText(message);
    }
}
