package app.controllers;

import app.model.IDatabaseModel;
import app.model.IUserModel;
import app.model.Practisable;
import app.model.PractiseListModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A SceneLoader object represents an FXMLLoader that when given the path to an FXML
 * file upon construction, can either be told to open a new scene window or switch scenes
 * on the current window.
 *
 * A SceneLoader object acts as a wrapper around an FXMLLoader and provides an abstraction
 * from the opening scene and switching scene operations that must be performed on an FXMLLoader.
 */
public class SceneLoader {
    private static final String ERROR_SCENE = "/app/views/ErrorScene.fxml";
    private static final String STREAK_SCENE = "/app/views/StreakScene.fxml";

    private FXMLLoader _loader;
    private Parent _playerParent;

    public SceneLoader(String scenePath) {
        initialiseLoader(scenePath);
    }

    public SceneLoader(){};

    /**
     * Returns the controller associated with the scene that was supplied to this object
     * upon construction.
     * @param <T>
     * @return T the controller type associated with the FXML file.
     */
    public <T> T getController() {
        return _loader.getController();
    }

    /**
     * From the _playerParent object, opens a new scene for this object in a new window
     * keeping the old scene still open.
     */
    public void openScene() {

        // create new scene and window
        Scene playerScene = new Scene(_playerParent);
        Stage window = new Stage();

        // display to the user
        window.setScene(playerScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
    }

    /**
     * Given an event, closes the old scene from which this event originated
     * from and moves to the new scene that was supplied to this object
     * upon construction.
     * @param event
     */
    public void switchScene(ActionEvent event) {
        // switch scenes
        Scene playerScene = new Scene(_playerParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }

    /**
     * Opens a new error window for the user displaying the given error message
     * to indicate what they have done wrong.
     * @param message
     */
    public void loadErrorMessage(String message) {
        initialiseLoader(ERROR_SCENE);

        ErrorSceneController controller = _loader.getController();
        controller.setMessage(message);

        openScene();
    }

    /**
     * Given an IUserModel object, the daily streak that is stored in this object
     * is displayed in the streak scene to the user.
     * @param userModel
     */

    public void loadStreakMessage(IUserModel userModel) {
        initialiseLoader(STREAK_SCENE);

        StreakSceneController controller = _loader.getController();
        controller.setModel(userModel);

        openScene();
    }


    /**
     * Given a path to a scene, initialises the FXMLLoader of this SceneLoader to load
     * the specified scene. This means the controller and views loaded by this object will
     * be of this scene.
     * @param scenePath
     */
    private void initialiseLoader(String scenePath) {
        _loader = new FXMLLoader();
        _loader.setLocation(getClass().getResource(scenePath));

        _playerParent = null;
        try {
            _playerParent = _loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
