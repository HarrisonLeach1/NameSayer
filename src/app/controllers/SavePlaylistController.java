package app.controllers;

import app.model.ConcatenatedName;
import app.model.DatabaseModel;
import app.model.IDatabaseModel;
import com.sun.xml.internal.bind.v2.model.core.ID;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.util.List;

public class SavePlaylistController {

    @FXML private TextField _playListName;

    private List<ConcatenatedName> _playlist;
    private IDatabaseModel _databaseModel;

    public void setModel(IDatabaseModel databaseModel) {
        _databaseModel = databaseModel;
    }

    public void givePlayList(List<ConcatenatedName> playlist) {
        _playlist = playlist;
    }

    public void handleConfirmAction(ActionEvent event) {
        _databaseModel.savePlaylist(_playlist, _playListName.getText());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    public void handleReturnAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}
