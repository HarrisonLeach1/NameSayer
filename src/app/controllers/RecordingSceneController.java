package app.controllers;

import app.model.IPractiseListModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static app.model.Recording.RECORD_TIME;

public class RecordingSceneController implements Initializable {

    @FXML private Button _startBtn, _cancelBtn;
    @FXML private ProgressBar _progressBar = new ProgressBar(0);

    Task recordWorker;
    IPractiseListModel _practiseListModel;

    /**
     * Initialises the contoller with the practise list model to be used.
     * @param model
     */
    public void initModel(IPractiseListModel model) {
        _practiseListModel = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _cancelBtn.setDisable(true);
    }

    /**
     * Starts a new user recording which lasts for the specified record time.
     * The users voice is recorded and the time left to record is indicated
     * by the progress bar.
     */
    public void startButtonPressed() {
        // tell model to create recording
        _practiseListModel.createUserRecording();

        _startBtn.setDisable(true);
        _progressBar.setProgress(0);
        _cancelBtn.setDisable(false);

        recordWorker = startWorker();

        // bind worker to display progress bar updates
        _progressBar.progressProperty().unbind();
        _progressBar.progressProperty().bind(recordWorker.progressProperty());

        // when the specified recording time is finished, close the window
        recordWorker.setOnSucceeded(event -> {
            Stage window = (Stage)_cancelBtn.getScene().getWindow();
            window.close();
        });

        new Thread(recordWorker).start();
    }

    /**
     * Cancels the new user recording, closing the recording window.
     */
    public void cancelButtonPressed() {
        _startBtn.setDisable(false);
        _cancelBtn.setDisable(true);

        recordWorker.cancel(true);
        _progressBar.progressProperty().unbind();
        _progressBar.setProgress(0);

        _practiseListModel.cancelRecording();

        Stage window = (Stage)_cancelBtn.getScene().getWindow();
        window.close();
    }

    /**
     * Task which updates the progress bar for the specified recording time.
     * Should be executed on a new Thread.
     */
    public Task startWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < RECORD_TIME * 10; i++) {
                    Thread.sleep(100);
                    updateProgress(i+1,50);
                }
                return true;
            }
        };

    }


}
