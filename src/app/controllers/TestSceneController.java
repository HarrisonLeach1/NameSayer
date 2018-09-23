package app.controllers;

import app.model.Name;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static app.model.DataModel.USER_DATABASE;
import static app.model.Recording.RECORD_TIME;

public class TestSceneController implements Initializable {

    @FXML private Button _playBtn,_okBtn;
    @FXML private ProgressBar _progress;

    public static final String TEST_FILE = ".test.wav";

    /**
     * When the test mic scene opens the users mic is recorded for five seconds.
     * The progress bar is updated to indicate how much time is left for testing.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String cmd = "mkdir -p " + USER_DATABASE + "; ffmpeg -y -f alsa -t " + RECORD_TIME + " -i default "+ USER_DATABASE + TEST_FILE;

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // create new timeline which updates the progress bar for 5 seconds
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(_progress.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(5), e -> {
                    _playBtn.setDisable(false);
                    _okBtn.setDisable(false);
                }, new KeyValue(_progress.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }


    /**
     * Deletes the test recording when the user exits the window.
     * @param event
     */
    public void handleReturnAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = new File(USER_DATABASE + TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        window.close();
    }

    /**
     * Plays back the test recording to the user.
     */
    public void handlePlayAction(ActionEvent event) {
        try {
            String cmd = "ffplay " + USER_DATABASE + TEST_FILE + " -autoexit -nodisp";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}