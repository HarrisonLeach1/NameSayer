package app.controllers;

import app.model.ConcatenatedName;
import app.model.DataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.util.List;

public class SavePlaylistController {

    @FXML private TextField _playListName;

    private List<ConcatenatedName> _playlist;

    public void givePlayList(List<ConcatenatedName> playlist) {
        _playlist = playlist;
    }

    public void handleConfirmAction(ActionEvent event) {
        DataModel.getInstance().savePlaylist(_playlist, _playListName.getText());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    public void handleReturnAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}
