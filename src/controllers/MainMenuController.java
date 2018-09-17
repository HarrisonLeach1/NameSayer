package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private Pane data_pane, rec_pane;

    @FXML
    private Button view_data_btn,view_rec_btn,test_mic_btn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void handleMenuAction(ActionEvent event) throws IOException {
        if(event.getSource() == view_data_btn){
            data_pane.toFront();
        } else if(event.getSource() == view_rec_btn){
            rec_pane.toFront();
        } else if(event.getSource() == test_mic_btn){
            Parent playerParent = FXMLLoader.load(getClass().getResource("/app/RecordingScene.fxml"));
            Scene playerScene = new Scene(playerParent);

            Stage window = new Stage();
            window.setScene(playerScene);
            window.initModality(Modality.APPLICATION_MODAL);
            window.showAndWait();
        }
    }

    public void handleStartAction(ActionEvent event) throws IOException {
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/PlayScene.fxml"));
        Scene playerScene = new Scene(playerParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }
}
