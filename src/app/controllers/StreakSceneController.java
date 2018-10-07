package app.controllers;

import app.model.DataModel;
import app.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A StreakSceneController holds the responsibility of receiving input events
 * from the user while the streak window is open. It then translates them
 * into actions on the views.
 */

public class StreakSceneController implements Initializable {

    @FXML private Label _streakCounter;

    /**
     * Retrieves the users daily streak information from the data model.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _streakCounter.setText(String.valueOf(DataModel.getInstance().getDailyStreak()));
    }

    /**
     * When the okay button is pressed the streak window is closed.
     * @param event
     */
    public void okButtonAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}