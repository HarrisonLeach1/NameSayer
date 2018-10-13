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

    @FXML private Button _startBtn, _finishBtn, _cancelBtn;
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
        _finishBtn.setDisable(true);
    }

    /**
     * Starts a new user recording which lasts for the specified record time.
     * The users voice is recorded and the time left to record is indicated
     * by the progress bar.
     */
    public void recordButtonPressed() {
        // tell model to create recording
        _practiseListModel.createUserRecording();

        _startBtn.setDisable(true);
        _progressBar.setProgress(0);
        _cancelBtn.setDisable(false);
        _finishBtn.setDisable(false);

        recordWorker = recordWorker();

        // bind worker to display progress bar updates
        _progressBar.progressProperty().bind(recordWorker.progressProperty());

        // when the specified recording time is finished, close the window
        recordWorker.setOnSucceeded(event -> {
            _progressBar.progressProperty().unbind();
            Stage window = (Stage)_startBtn.getScene().getWindow();
            window.close();
        });

        new Thread(recordWorker).start();
    }

    public void finishButtonPressed() {
        recordWorker.cancel(true);
        _practiseListModel.finishUserRecording();
        Stage window = (Stage)_startBtn.getScene().getWindow();
        window.close();
    }

    /**
     * Cancels the new user recording and deletes the recording file.
     * Closes the recording window.
     */
    public void cancelButtonPressed() {
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
    private Task recordWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                Thread.sleep(RECORD_TIME * 1000);
                return true;
            }
        };

    }


}
