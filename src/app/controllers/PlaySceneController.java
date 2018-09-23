package app.controllers;

import app.model.IPractiseListModel;
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
import java.util.ResourceBundle;

public class PlaySceneController implements Initializable {

    @FXML private Button keep_btn, compare_btn;
    @FXML private Label _displayName, _bad_Label, _savedLabel;

    private IPractiseListModel _practiseListModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Loads in the practise list model that stores the list of selected names from
     * the main menu to be practised.
     * @param practiseListModel
     */
    public void initModel(IPractiseListModel practiseListModel) {
        _practiseListModel = practiseListModel;
        _displayName.setText("Name: " + _practiseListModel.nextName().toString());
    }

    /**
     * Moves to the next Name in the list and updates the displayed name. The Name is
     * unchanged if the end of the list is reached.
     */
    public void nextButtonPressed() {
        _savedLabel.setVisible(false);
        _bad_Label.setVisible(false);
        _displayName.setText("Name: " + _practiseListModel.nextName().toString());

        keep_btn.setDisable(true);
        compare_btn.setDisable(true);

    }

    /**
     * Moves to the previous Name in the list and updates the displayed name. The Name
     * is unchanged if there are no previous names.
     */
    public void previousButtonPressed() {
        _savedLabel.setVisible(false);
        _bad_Label.setVisible(false);
        _displayName.setText("Name: " + _practiseListModel.previousName().toString());
    }

    public void keepButtonPressed() {
        _savedLabel.setVisible(true);
        //_practiseListModel.keepRecording();
    }

    /**
     * When the recording button is pressed the recording scene is displayed to the user
     */
    public void recordButtonPressed() {
        Parent playerParent = null;
        FXMLLoader loader = new FXMLLoader();

        // load in the recording scene
        try {
            loader.setLocation(getClass().getResource("/app/views/RecordingScene.fxml"));
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // pass the model to the recording scene controller
        RecordingSceneController controller = loader.getController();
        controller.initModel(_practiseListModel);

        Scene playerScene = new Scene(playerParent);

        // switch scenes
        Stage window = new Stage();
        window.setScene(playerScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();

        // enable buttons
        keep_btn.setDisable(false);
        compare_btn.setDisable(false);

    }

    /**
     * Plays the currently displayed name
     */
    public void playButtonPressed() {
        _practiseListModel.playCurrentName();
    }

    /**
     * Redirects the user back to the main menu
     */
    public void handleReturnAction(ActionEvent event) throws IOException {
        // load in the main menu scene
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/NameSayer.fxml"));
        Scene playerScene = new Scene(playerParent);

        // switch scenes
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
