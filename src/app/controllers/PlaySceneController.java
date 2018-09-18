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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class PlaySceneController implements Initializable {

    @FXML
    private Button play_btn,next_btn,bad_btn,return_btn;

    private IPractiseListModel _practiseListModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initModel(IPractiseListModel practiseListModel) {
        _practiseListModel = practiseListModel;
    }


    public void handleReturnAction(ActionEvent event) throws IOException {
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/NameSayer.fxml"));
        Scene playerScene = new Scene(playerParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }
}
