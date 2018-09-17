package app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RecordingSceneController implements Initializable {

    @FXML
    private Button ok_btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void handleReturnAction(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

}
