package app.controllers;

import app.model.ConcatenatedName;
import app.model.IDatabaseModel;
import app.model.Practisable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.util.List;

/**
 * A SavePlaylistController holds the responsibility of receiving input events
 * from the user while they are inputting a name for their saved playlist.
 * It then translates this input to actions on the model using the given playlist.
 */
public class SavePlaylistController {

    @FXML private TextField _playListName;
    private List<Practisable> _playlist;
    private IDatabaseModel _databaseModel;

    public void setModel(IDatabaseModel databaseModel) {
        _databaseModel = databaseModel;
    }

    /**
     * The user is confirming to save the playlist so the text from the text field
     * is validated and the given playlist is saved under this name. The window is
     * also closed.
     * @param event
     */
    public void handleConfirmAction(ActionEvent event) {
        // tell the data model to save the playlist somewhere
        _databaseModel.savePlaylist(_playlist, _playListName.getText());

        // close window
        _databaseModel.savePlaylist(_playlist, _playListName.getText());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * The user has decide to abort the saving of the playlist and the window is
     * closed without saving the given playlist.
     * @param event
     */
    public void handleReturnAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Sets the playlist which is to be named in this controller.
     * @param playlist
     */
    public void setPlayList(List<Practisable> playlist) {
        _playlist = playlist;
    }
}
