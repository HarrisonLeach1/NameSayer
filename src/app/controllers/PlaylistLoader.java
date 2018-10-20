package app.controllers;

import app.model.IDatabaseModel;
import app.model.IUserModel;
import app.model.Practisable;
import app.model.PractiseListModel;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A PlaylistLoader object represents a loader used for loading in a playlist from a
 * controller and moving this playlist along to the play scene.
 *
 * This provides an abstraction from the logic that occurs when determining whether or not
 * a playlist should be moved onto the play scene.
 */
public class PlaylistLoader {
    private static final String CONFIRM_SCENE = "/app/views/ConfirmScene.fxml";
    private static final String CONFIRM_MSG = "Could not find the following name(s): \n\n";
    private static final String PLAY_SCENE = "/app/views/PlayScene.fxml";
    private final List<Practisable> _playlist;
    private final IDatabaseModel _databaseModel;
    private final IUserModel _userModel;

    public PlaylistLoader(List<Practisable> playlist, IDatabaseModel databaseModel, IUserModel userModel) {
        _playlist = playlist;
        _databaseModel = databaseModel;
        _userModel = userModel;
    }

    /**
     * Determines whether or not this playlist should be continued on to the play scene by
     * investigating the playlist or confirming with the user.
     * If it is successful, the scene from which the given event came from is closed and the
     * play scene is opened.
     * @param event
     */
    public void moveToPlayScene(ActionEvent event) {
        String missingNames = _databaseModel.compileMissingNames(_playlist);

        if (missingNames.isEmpty() || confirmMessageWithUser(CONFIRM_MSG + missingNames)) {
            SceneLoader loader = new SceneLoader(PLAY_SCENE);

            PlaySceneController controller = loader.getController();
            controller.setModel(new PractiseListModel(new ArrayList<>(_playlist)), _userModel, _databaseModel);

            loader.switchScene(event);
        }
    }

    /**
     * Given a message, opens a new window displaying the message to the user asking them
     * to accept or decline the message proposal. Returns, true if the user confirms. Or
     * false if the user declines.
     * @param message
     * @return whether or not the user said yes to the given message
     */
    private boolean confirmMessageWithUser(String message) {
        SceneLoader loader = new SceneLoader(CONFIRM_SCENE);

        ConfirmSceneController controller = loader.getController();
        controller.setMessage(message);

        loader.openScene();

        if(controller.saidYes()) {
            return true;
        }
        return false;
    }
}
