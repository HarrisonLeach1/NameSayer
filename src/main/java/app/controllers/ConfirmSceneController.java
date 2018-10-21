package app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * An ConfirmSceneController holds the responsibility of receiving input events
 * from the user while the confirm window is open. It is responsible for updating
 * the confirm message displayed to the user, and also translating the users
 * decision to actions on the app.views.
 */
public class ConfirmSceneController {
    @FXML private Label _confirmMessage;
    private boolean _saidYes = false;

    /**
     * Updates the user's decision to positive and closes the confirmation window.
     * @param event
     */
    public void yesButtonAction(ActionEvent event) {
        _saidYes = true;

        // close window
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Updates the user's decision to negative and closes the confirmation window.
     * @param event
     */
    public void noButtonAction(ActionEvent event) {
        _saidYes = false;

        // close window
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Given a message string, when the confirm window is loaded in it will display
     * this message to the user informing them of the decision they have to make.
     * @param message
     */
    public void setMessage(String message) {
        _confirmMessage.setText(message);
    }

    /**
     * Returns the users decision, indicates whether they pressed the yes, no, or
     * neither button.
     * @return
     */
    public boolean saidYes() {
        return _saidYes;
    }
}
