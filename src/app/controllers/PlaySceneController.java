package app.controllers;

import app.model.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A PlaySceneController holds the responsibility of receiving input events
 * from the user during name pronunciation practise and then translating
 * them into actions on the IPractiseListModel.
 *
 * The IPractiseListModel then passes information back to the PlaySceneController
 * to update the view.
 */
public class PlaySceneController implements DataModelListener, Initializable{

    @FXML private Button _keepBtn, _compareBtn, _prevBtn, _nextBtn, _badBtn;
    @FXML private Label _displayName, _bad_Label, _savedLabel, _dateTimeLabel , _levelCounter;
    @FXML private Slider _volumeSlider;
    @FXML private ProgressBar _levelProgress, _micLevelProgress;
    @FXML private ProgressBar _playBar;
    private Task _playing;

    private IPractiseListModel _practiseListModel;
    private Practisable _currentName;
    private boolean _firstComparison;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(test).start();
        _micLevelProgress.progressProperty().bind(test.progressProperty());
    }
    /**
     * Loads in the practise list model that stores the list of selected names from
     * the main menu to be practised.
     * @param practiseListModel
     */
    public void initModel(IPractiseListModel practiseListModel) {
        _practiseListModel = practiseListModel;
        _currentName = _practiseListModel.nextName();
        makeTransition();
        _volumeSlider.setMin(0);
        _volumeSlider.setMax(2.0);
        _volumeSlider.setValue(1.0);
        DataModel.getInstance().addListener(this);
    }

    /**
     * Moves to the next Name in the list and updates the displayed name. The Name is
     * unchanged if the end of the list is reached.
     */
    public void nextButtonPressed() {
        _currentName = _practiseListModel.nextName();
        makeTransition();
        stopProgress();
    }

    /**
     * Moves to the previous Name in the list and updates the displayed name. The Name
     * is unchanged if there are no previous names.
     */
    public void previousButtonPressed() {
        _currentName = _practiseListModel.previousName();
        makeTransition();
        stopProgress();
    }

    /**
     * Keeps the recording created by the user, prevents it from being deleted.
     */
    public void keepButtonPressed() {
        _savedLabel.setVisible(true);
        _practiseListModel.keepRecording();
    }

    /**
     * When the recording button is pressed the recording scene is displayed to the user
     */
    public void recordButtonPressed() {
        Parent playerParent = null;
        FXMLLoader loader = new FXMLLoader();

        // load in the recording scene
        try {
            loader.setLocation(getClass().getResource("/app/views/RecordingScene.fxml"));
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // pass the model to the recording scene controller
        RecordingSceneController controller = loader.getController();
        controller.initModel(_practiseListModel);

        Scene playerScene = new Scene(playerParent);

        // switch scenes
        Stage window = new Stage();
        window.setScene(playerScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();

        // enable buttons
        _keepBtn.setDisable(false);
        _compareBtn.setDisable(false);

    }

    /**
     * Plays the currently displayed name when the user presses the play button.
     */
    public void playButtonPressed() {
        _playing = playWorker();
        _playBar.progressProperty().bind(_playing.progressProperty());

        _playing.setOnSucceeded( e -> {
            stopProgress();
        });
        new Thread(_playing).start();

    }

    /**
     * Creates a new Task which allows the play funcitonality to be
     * executed on a new thread.
     */
    private Task playWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                // play user recording
                _currentName.playRecording(_volumeSlider.getValue());
                return true;
            }
        };

    }

    /**
     * Redirects the user back to the main menu when the return button is pressed.
     */
    public void handleReturnAction(ActionEvent event) throws IOException {
        // load in the main menu scene
        test.cancel();
        Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/NameSayer.fxml"));
        Scene playerScene = new Scene(playerParent);

        DataModel.getInstance().deleteTempDirectory();

        // switch scenes
        Stage window2 = (Stage)((Node)event.getSource()).getScene().getWindow();
        window2.setScene(playerScene);
    }

    /**
     * Plays the user's recording then the original recording.
     * Allows the user to judge their pronunciation.
     */
    public void compareButtonPressed() throws IOException {
        _playing = compareWorker();
        _playBar.progressProperty().bind(_playing.progressProperty());

        _playing.setOnSucceeded( e -> {
            if (_firstComparison) {
                openLevelScene();
                _firstComparison = false;
            }
            stopProgress();
        });

        new Thread(_playing).start();

    }

    /**
     * Creates a new Task which allows the comparison funcitonality to be
     * executed on a new thread.
     */
    private Task compareWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                // play user recording
                _practiseListModel.compareUserRecording(_volumeSlider.getValue());

                return true;
            }
        };

    }

    /**
     * Sets the currently display to be of bad quality.
     */
    public void badButtonPressed() throws IOException {
        _currentName.setBadQuality();
        _bad_Label.setVisible(true);
    }

    /**
     * Updates the _levelCounter to display the users current level.
     * Updates the _levelProgress to display the user experience progress towards
     * the next level.
     * @param experience
     */
    @Override
    public void notifyProgress(int experience) {
        int currentLevelProgress = experience % 100;
        int currentLevel = experience / 100;
        _levelProgress.setProgress(currentLevelProgress / 100.0);
        _levelCounter.setText(String.valueOf(currentLevel));

    }


    /**
     * Opens the level scene to the user which allows them to decide whether or not
     * they have pronounced the name well.
     */
    private void openLevelScene() {
        Parent playerParent = null;
        try {
            // load in scene
            playerParent = FXMLLoader.load(getClass().getResource("/app/views/LevelScene.fxml"));
            Scene playerScene = new Scene(playerParent);
            Stage window = new Stage();

            // open scene
            window.setScene(playerScene);
            window.initModality(Modality.APPLICATION_MODAL);
            window.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Whenever the user moves to a new name the scene is reinitialised.
     *
     */
    private void makeTransition() {
        _savedLabel.setVisible(false);
        _bad_Label.setVisible(false);

        // change displayed name
        _displayName.setText("Name: " + _currentName.toString());

        // decides whether or not to give users the ability to rate the recording
        if (_currentName.isRateable()) {
            _badBtn.setDisable(false);
        } else {
            _badBtn.setDisable(true);
        }
        _dateTimeLabel.setText(_currentName.getDateTimeCreated());

        // is set to true so that the user has the ability to gain experience again
        _firstComparison = true;

        checkBounds();
    }


    /**
     * Indicates when the start or the end of the list has been reached by disabling
     * the previous and/or next button.
     */
    private void checkBounds() {

        // if the user has reached the end of the list disable the next button, otherwise don't
        if (!_practiseListModel.hasNext()) {
            _nextBtn.setDisable(true);
        } else {
            _nextBtn.setDisable(false);
        }

        // if the user is at the start of the list disable the previous button, otherwise don't
        if (!_practiseListModel.hasPrevious()) {
            _prevBtn.setDisable(true);
        } else {
            _prevBtn.setDisable(false);
        }

        _keepBtn.setDisable(true);
        _compareBtn.setDisable(true);
    }

    /**
     * The progress bar is disabled to indicate that no audio is playing.
     */
    private void stopProgress(){
        _playBar.progressProperty().unbind();
        _playBar.setProgress(0);
        _playing.cancel();
    }

}
