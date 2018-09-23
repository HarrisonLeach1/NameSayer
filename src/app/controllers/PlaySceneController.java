package app.controllers;

import app.model.IPractiseListModel;
import app.model.Name;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class PlaySceneController implements Initializable {

    @FXML private Button play_btn,next_btn,bad_btn,return_btn;
    @FXML private Label _displayName, _bad_Label;

    private IPractiseListModel _practiseListModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initModel(IPractiseListModel practiseListModel) {
        _practiseListModel = practiseListModel;
        _displayName.setText("Name: " + _practiseListModel.nextName().toString());
    }

    public void nextButtonPressed() {
        _bad_Label.setVisible(false);
        if (_practiseListModel.hasNext()) {
            _displayName.setText("Name: " + _practiseListModel.nextName().toString());
        } else {
            // missing code to handle end of list reached
        }

    }

    public void keepButtonPressed() {
        _practiseListModel.keepRecording();
    }

    public void recordButtonPressed() {
        _practiseListModel.createUserRecording();

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

    public void badButtonPressed() throws IOException {
        _practiseListModel.setBadQuality();
        _bad_Label.setVisible(true);
    }

}
