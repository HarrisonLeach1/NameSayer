package app.controllers;

import app.model.IPractiseListModel;
import app.model.Name;
import app.model.PractiseListModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class PlaySceneController implements Initializable {

    @FXML private Button play_btn,next_btn,bad_btn,return_btn,keep_btn, compare_btn;
    @FXML private Label _displayName;

    private IPractiseListModel _practiseListModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initModel(IPractiseListModel practiseListModel) {
        _practiseListModel = practiseListModel;
        _displayName.setText("Name: " + _practiseListModel.nextName().toString());
    }

    public void nextButtonPressed() {
        _displayName.setText("Name: " + _practiseListModel.nextName().toString());

        keep_btn.setDisable(true);
        compare_btn.setDisable(true);

    }

    public void previousButtonPressed() {
        _displayName.setText("Name: " + _practiseListModel.previousName().toString());
    }

    public void keepButtonPressed() {
        //_practiseListModel.keepRecording();
    }

    public void recordButtonPressed() {
        Parent playerParent = null;
        FXMLLoader loader = new FXMLLoader();

        try {
            loader.setLocation(getClass().getResource("/app/views/RecordingScene.fxml"));
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecordingSceneController controller = loader.getController();
        controller.initModel(_practiseListModel);

        Scene playerScene = new Scene(playerParent);

        Stage window = new Stage();
        window.setScene(playerScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();

        keep_btn.setDisable(false);
        compare_btn.setDisable(false);

    }


    public void playButtonPressed() {
        _practiseListModel.playCurrentName();
    }

    public void handleReturnAction(ActionEvent event) throws IOException {
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/NameSayer.fxml"));
        Scene playerScene = new Scene(playerParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }

    public void compareButtonPressed() {
        _practiseListModel.compareUserRecording();
    }
}
