package app.controllers;

import app.model.UserModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StreakSceneController implements Initializable {

    @FXML private Label _streakCounter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _streakCounter.setText(String.valueOf(UserModel.getInstance().getDailyStreak()));
    }

    public void okButtonAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}
