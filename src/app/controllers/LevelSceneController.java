package app.controllers;

import app.model.DataModel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class LevelSceneController {

    public void yesButtonPressed(ActionEvent event) {
        DataModel.getInstance().updateUserXP();
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    public void noButtonPressed(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}
