package app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A SceneLoader object represents an FXMLLoader that when given the path to an FXML
 * file upon construction, can either be told to open a new scene window or switch scenes
 * on the current window.
 *
 * A SceneLoader object acts as a wrapper around an FXMLLoader and provides an abstraction
 * from the opening scene and switching scene operations that must be performed on an FXMLLoader.
 */
public class SceneLoader {

    private final FXMLLoader _loader;
    private Parent _playerParent;

    public SceneLoader(String scenePath) {
        _loader = new FXMLLoader();
        _loader.setLocation(getClass().getResource(scenePath));

        _playerParent = null;
        try {
            _playerParent = _loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}
