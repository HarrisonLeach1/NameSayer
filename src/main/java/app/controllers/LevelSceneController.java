package app.controllers;

import app.model.IUserModel;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A LevelSceneController holds the responsibility of receiving input events
 * from the user while the error window is open it then translates them
 * into actions on the app.views.
 */
public class LevelSceneController {

    private static final int WAIT_BEFORE_CLOSE = 2;
    private static final String LEVEL_UP_MSG = "Well done! You are now level ";
    private static final String GAIN_XP_MSG = "You have gained ";
    @FXML private Button _goodBtn, _avgBtn, _badBtn;
    @FXML private Label _messageLabel;
    private IUserModel _userModel;

    /**
     * Sets the IUserModel that this level scene communicates with.
     */
    public void setModel(IUserModel userModel) {
        _userModel = userModel;
    }

    /**
     * Confirms that the user has made a good or average pronunciation of the name.
     * The IUserModel is updated to increase the experience level of the user.
     * The user is notified of the experience gain they have made. The window
     * is closed and the user is displayed the play scene again.
     * @param event
     */
    public void goodButtonPressed(ActionEvent event) {
        disableButtons();
        IUserModel.ComparisonRating rating;

        // find rating based on the button that was pressed
        if (event.getSource() == _goodBtn) {
            rating = IUserModel.ComparisonRating.Good;
        } else {
            rating = IUserModel.ComparisonRating.Average;
        }

        updateMessageLabel(rating);

        waitAndClose(WAIT_BEFORE_CLOSE);
    }

    /**
     * This updates the message label depending on the rating the user has given
     * their pronunciation of the recording. The user can either receive a level
     * up or experience gained message.
     */
    private void updateMessageLabel(IUserModel.ComparisonRating rating) {
        int previousLevel = _userModel.getUserLevel();

        _userModel.updateUserXP(rating);

        int currentLevel =_userModel.getUserLevel();

        // if level has increased, notify user of level up. Otherwise notify user of experience gained.
        if (currentLevel > previousLevel) {
            _messageLabel.setText(LEVEL_UP_MSG + currentLevel +"!");
        } else {
            _messageLabel.setText(GAIN_XP_MSG + rating.getExperience() + " XP");
        }
    }


    /**
     * The user has not made a good pronunciation of the name.
     * As such they are not rewarded any experience.
     * The window is closed and the user is displayed the play scene again.
     * @param event
     */
    public void badButtonPressed(ActionEvent event) {
        waitAndClose(0);
    }

    /**
     * Waits for the specified amount of time to close the window.
     */
    private void waitAndClose(int waitTime) {
        Stage window = (Stage) _messageLabel.getScene().getWindow();
        PauseTransition delay = new PauseTransition(Duration.seconds(waitTime));
        delay.setOnFinished( e -> window.close() );
        delay.play();
    }

    /**
     * Disables all buttons on this scene, preventing the user from making
     * multiple ratings.
     */
    private void disableButtons() {
        _goodBtn.setDisable(true);
        _avgBtn.setDisable(true);
        _badBtn.setDisable(true);
    }
}
