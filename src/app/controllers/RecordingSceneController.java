package app.controllers;

import app.model.IPractiseListModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import static app.model.Recording.RECORD_TIME;

/**
 * A RecordingSceneController holds the responsibility of receiving input events
 * from the user during name pronunciation practise and then translating
 * them into actions on the IPractiseListModel.
 */
public class RecordingSceneController {

    @FXML private Button _startBtn, _cancelBtn;
    @FXML private ProgressBar _progressBar = new ProgressBar(0);

    private Task recordWorker;
    private IPractiseListModel _practiseListModel;

    /**
     * Initialises the controller with the practise list model to be used.
     * @param model
     */
    public void initModel(IPractiseListModel model) {
        _practiseListModel = model;
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
                for (int i = 0; i < RECORD_TIME * 100; i++) {
                    Thread.sleep(10);
                    updateProgress(i+1,500);
                }
                return true;
            }
        };

    }


}
