package app.controllers;

import app.model.DataModel;
import app.model.IUserModel;
import app.model.UserModel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * A LevelSceneController holds the responsibility of receiving input events
 * from the user while the error window is open it then translates them
 * into actions on the views.
 */
public class LevelSceneController {

    /**
     * Confirms that the user has made a good pronunciation of the name.
     * The IUserModel is updated to increase the experience level of the user.
     * The user is notified of the experience gain they have made. The window
     * is closed and the user is displayed the play scene again.
     * @param event
     */
    public void goodButtonPressed(ActionEvent event) {
        UserModel.getInstance().updateUserXP(IUserModel.ComparisonRating.Good);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * The user has not made a good pronunciation of the name.
     * As such they are not rewarded any experience.
     * The window is closed and the user is displayed the play scene again.
     * @param event
     */
    public void badButtonPressed(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}
