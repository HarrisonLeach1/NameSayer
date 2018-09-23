package app.controllers;

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
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class TestSceneController implements Initializable {

    @FXML
    private Button play_btn,ok_btn;
    @FXML
    private ProgressBar _progress;

    private static final String USER_DATABASE = "./userRecordings/";
    private static final int RECORD_TIME = 5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String cmd = "mkdir -p " + USER_DATABASE + "; ffmpeg -y -f alsa -t " + RECORD_TIME + " -i default _test.wav";

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
            builder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(_progress.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(5), e -> {
                    play_btn.setDisable(false);
                    ok_btn.setDisable(false);
                }, new KeyValue(_progress.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }


    public void handleReturnAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = new File("./userRecordings/_test.wav");
        if (file.exists()) {
            file.delete();
        }
        window.close();
    }

    public void handlePlayAction(ActionEvent event) {
        InputStream in;
        try {
            in = new FileInputStream("./userRecordings/_test.wav");
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}