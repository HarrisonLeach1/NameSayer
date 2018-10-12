package app.controllers;

import app.model.ConcatenatedName;
import app.model.DataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.util.List;

/**
 * A SavePlaylistController holds the responsibility of receiving input events
 * from the user while the they are choosing a name for the playlist they wish to save.
 * It then translates them into actions on the views.
 */
public class SavePlaylistController {

    @FXML private TextField _playListName;
    private List<ConcatenatedName> _playlist;

    /**
     * This controller receives information of the playlist of names to be saved.
     * @param playlist
     */
    public void setPlayList(List<ConcatenatedName> playlist) {
        _playlist = playlist;
    }

    /**
     * When the user confirms the name of the text file to which the playlist should be
     * saved, the given playlist of names is saved to the specified file name.
     * @param event
     */
    public void handleConfirmAction(ActionEvent event) {
        if (!_playListName.getText().isEmpty()) {
            DataModel.getInstance().savePlaylist(_playlist, _playListName.getText());
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.close();
        }
    }

    /**
     * When the user presses cancel, the window is closed without the playlist being
     * saved.
     * @param event
     */
    public void handleReturnAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }
}
