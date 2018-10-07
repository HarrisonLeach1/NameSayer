package app.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A TestSceneController holds the responsibility of receiving input events
 * from the user while the test window is open. It then translates them
 * into actions on the views.
 */
public class TestSceneController {

    @FXML
    private ProgressBar _progress;
    /**
     * When the test mic scene opens the users mic is recorded for five seconds.
     * The progress bar is updated to indicate how much time is left for testing.
     */
    Task test = new Task() {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine targetLine;
        {
            try {
                targetLine = (TargetDataLine) AudioSystem.getLine(info);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected Object call() {
            try {
                //Microphone
                targetLine.open();
                targetLine.start();

                byte[] data = new byte[targetLine.getBufferSize() / 5];

                while (true) {
                    targetLine.read(data, 0, data.length);
                    int level = calculateRMSLevel(data);
                    updateProgress(level,100);
                }
            } catch (LineUnavailableException lue) {
                lue.printStackTrace();
            }
            return null;
        }

        @Override
        protected void cancelled() {
            targetLine.stop();
            targetLine.close();
            super.cancelled();
        }


        @Override
        protected void updateProgress(double workDone, double max) {
            super.updateProgress(workDone, max);
        }
    };
    /**
     * Calculates the microphone input and turns it into an integer
     */
    public static int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for (int j = 0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }

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
     * Plays back the test recording to the user.
     */
    public void handlePlayAction(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setOnCloseRequest(event1 -> endTest());
        new Thread(test).start();
        _progress.progressProperty().bind(test.progressProperty());
    }

    /**
     * Safely ends the microphone input loop
     */
    public void endTest(){
        test.cancel();
    }
}