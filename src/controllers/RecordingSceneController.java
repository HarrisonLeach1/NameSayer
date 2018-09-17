package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RecordingSceneController implements Initializable {

    @FXML
    private Button ok_btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void handleReturnAction(ActionEvent event) throws IOException {
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/NameSayer.fxml"));
        Scene playerScene = new Scene(playerParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }

}
