package app.controllers;

import app.model.MicTestTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

/**
 * A TestSceneController holds the responsibility of receiving input events
 * from the user while the test window is open. It then translates them
 * into actions on the app.views.
 */
public class TestSceneController {

    @FXML
    private ProgressBar _progress;
    private MicTestTask _micTest = new MicTestTask();

    /**
     * Ends the test and closes the window.
     * @param event
     */
    public void handleReturnAction(ActionEvent event) {
        endTest();
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Starts the microphone test.
     */
    public void handlePlayAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setOnCloseRequest(event1 -> endTest());
        new Thread(_micTest).start();
        _progress.progressProperty().bind(_micTest.progressProperty());
    }

    /**
     * Safely ends the microphone input loop
     */
    public void endTest(){
        _micTest.cancel();
    }
}