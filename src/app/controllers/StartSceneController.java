package app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StartSceneController {
    public void handleStartAction(ActionEvent event) throws IOException {
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/NameSayer.fxml"));
        Scene playerScene = new Scene(playerParent);

        // switch scenes
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);

        openStreakWindow();
    }

    public void handleQuitAction(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    private void openStreakWindow() {
        Parent playerParent = null;
        try {
            playerParent = FXMLLoader.load(getClass().getResource("/app/views/StreakScene.fxml"));
            Scene playerScene = new Scene(playerParent);
            Stage window = new Stage();

            window.setScene(playerScene);
            window.initModality(Modality.APPLICATION_MODAL);
            window.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
