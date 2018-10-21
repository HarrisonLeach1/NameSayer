package app.controllers;

import app.model.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static app.controllers.MainMenuController.USER_MANUAL;

/**
 * A PlaySceneController holds the responsibility of receiving input events
 * from the user during name pronunciation practise and then translating
 * them into actions on the IPractiseListModel.
 *
 * The IPractiseListModel then passes information back to the PlaySceneController
 * to update the view.
 */
public class PlaySceneController implements UserModelListener, Initializable {
    private static final double MIN_VOLUME = 0;
    private static final double MAX_VOLUME = 2.0;
    private static final double INITIAL_VOLUME = 1.0;
    private static final String MISSING_MSG = "Record yourself to contribute to this name! \nMissing audio: \n";
    private static final String RECORDING_SCENE = "/views/RecordingScene.fxml";
    private static final String MAIN_MENU_SCENE = "/views/NameSayer.fxml";

    private static final int MIN_LOOPS = 1;
    private static final int MAX_LOOPS = 10;
    private static final int DEFAULT_LOOPS = 3;


    @FXML private Button _keepBtn, _compareBtn, _prevBtn, _nextBtn, _badBtn, _playBtn, _stopBtn, _recordBtn;
    @FXML private Label _displayName, _badLabel, _savedLabel, _dateTimeLabel , _levelCounter, _missingNamesLabel;
    @FXML private Slider _volumeSlider;
    @FXML private ProgressBar _levelProgress, _micLevelProgress;
    @FXML private ProgressBar _playBar;
    @FXML private Spinner _loopSpinner;

    private Task _playing;
    private IPractiseListModel _practiseListModel;
    private IUserModel _userModel;
    private IDatabaseModel _databaseModel;

    private Practisable _currentName;
    private boolean _firstComparison;
    private MicTestTask _micTest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _micTest = new MicTestTask();
        new Thread(_micTest).start();

        _micLevelProgress.progressProperty().bind(_micTest.progressProperty());
        SpinnerValueFactory<Integer> loopValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_LOOPS,MAX_LOOPS,DEFAULT_LOOPS);
        this._loopSpinner.setValueFactory(loopValueFactory);
    }
    /**
     * Loads in the practise list app.model that stores the list of selected Practisable
     * objects from the main menu to be practised.
     * @param practiseListModel
     */
    public void setModel(IPractiseListModel practiseListModel, IUserModel userModel, IDatabaseModel databaseModel) {
        _practiseListModel = practiseListModel;
        _userModel = userModel;
        _databaseModel = databaseModel;

        _currentName = _practiseListModel.nextName();
        makeTransition();
        initialiseVolume();
        _userModel.addListener(this);
    }

    /**
     * Initialises the default max, min and initial volume for the volume bar when
     * the user first enters the play scene.
     */
    private void initialiseVolume() {
        _volumeSlider.setMin(MIN_VOLUME);
        _volumeSlider.setMax(MAX_VOLUME);
        _volumeSlider.setValue(INITIAL_VOLUME);
    }

    /**
     * Moves to the next Name in the list and updates the displayed name. The Name is
     * unchanged if the end of the list is reached.
     */
    public void nextButtonPressed() {
        _currentName = _practiseListModel.nextName();
        makeTransition();
    }

    /**
     * Moves to the previous Name in the list and updates the displayed name. The Name
     * is unchanged if there are no previous names.
     */
    public void previousButtonPressed() {
        _currentName = _practiseListModel.previousName();
        makeTransition();
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
        SceneLoader loader = new SceneLoader(RECORDING_SCENE);

        // pass the app.model to the recording scene controller
        RecordingSceneController controller = loader.getController();
        controller.setModel(_practiseListModel);

        loader.openScene();

        updateUserComparisonButtons();
    }

    /**
     * Plays the currently displayed name when the user presses the play button.
     * Executes the playing of the audio on a new thread to avoid GUI unresponsiveness.
     */
    public void playButtonPressed() {
        _playing = _practiseListModel.playTask(_volumeSlider.getValue());
        _playBar.progressProperty().bind(_playing.progressProperty());
        _stopBtn.toFront();

        _playing.setOnSucceeded(e -> {
            endAudio();
        });
        new Thread(_playing).start();

    }

    /**
     * Stops the currently playing name from playing when the user presses the
     * stop button.
     */
    public void stopButtonPressed() {
        _practiseListModel.stopPlayTask();
        endAudio();
    }

    /**
     * Redirects the user back to the main menu when the return button is pressed.
     */
    public void handleReturnAction(ActionEvent event){
        // load in the main menu scene
        _micTest.cancel();
        MainMenuController.setStartFalse();

        SceneLoader loader = new SceneLoader(MAIN_MENU_SCENE);

        // give model references back to the main controller, and give the playlist back
        MainMenuController controller = loader.getController();
        controller.setModel(_databaseModel, _userModel);
        controller.setPlaylist(_practiseListModel.getPractiseList());

        _databaseModel.deleteTempRecordings();

        loader.switchScene(event);
    }

    /**
     * Plays the user's recording then the original recording, this is repeated for
     * the amount of times specified by the user through the spinner.
     */
    public void compareButtonPressed() {
        // get value from spinner
        int loops = (int) _loopSpinner.getValue();
        _playing = _practiseListModel.compareUserRecordingTask(_volumeSlider.getValue(), loops);
        _playBar.progressProperty().bind(_playing.progressProperty());


        _compareBtn.setDisable(true);
        _recordBtn.setDisable(true);

        _playing.setOnSucceeded(e -> {
            if (_firstComparison) {
                openLevelScene();
                _firstComparison = false;
            }
            _recordBtn.setDisable(false);
            _compareBtn.setDisable(false);
            endAudio();
        });
        new Thread(_playing).start();
    }

    /**
     * Sets the currently display to be of bad quality. It is written to a
     * text file
     */
    public void badButtonPressed() throws IOException {
        _currentName.setBadQuality();
        _badLabel.setVisible(true);
    }

    /**
     * Updates the _levelCounter to display the users current level.
     * Updates the _levelProgress to display the user experience progress towards
     * the next level.
     * @param currentUserLevel
     * @param currentLevelProgress
     */
    @Override
    public void notifyProgress(int currentUserLevel, double currentLevelProgress) {
        _levelProgress.setProgress(currentLevelProgress);
        _levelCounter.setText(String.valueOf(currentUserLevel));
    }


    /**
     * Opens the level scene to the user which allows them to decide whether or not
     * they have pronounced the name well.
     */
    private void openLevelScene() {
        SceneLoader loader = new SceneLoader("/views/LevelScene.fxml");

        LevelSceneController controller = loader.getController();
        controller.setModel(_userModel);

        loader.openScene();
    }

    /**
     * Whenever the user moves to a new name the scene is reinitialised.
     */
    private void makeTransition() {
        updateUserComparisonButtons();
        endAudio();
        updateMissingNames();
        updateLabels();
        updateRatingButton();
        checkBounds();

        _practiseListModel.stopPlayTask();
        // is set to true so that the user has the ability to gain experience again
        _firstComparison = true;
    }

    /**
     * Updates the Label to display which Name is currently being practised and updates
     * its date/time information. Also removes the save and rating labels.
     */
    private void updateLabels() {
        // change displayed name and its time of creation
        _displayName.setText("Name: " + _currentName.toString());
        _dateTimeLabel.setText(_currentName.getDateTimeCreated());

        _savedLabel.setVisible(false);
        _badLabel.setVisible(false);
    }

    /**
     * Updates the rating button depending on whether or not the current name being
     * practised can be rated or not.
     */
    private void updateRatingButton() {
        // decides whether or not to give users the ability to rate the recording
        if (_currentName.isRateable()) {
            _badBtn.setDisable(false);
        } else {
            _badBtn.setDisable(true);
        }
    }

    /**
     * Updates the missing names label to indicate to the user whether or not a recording
     * file of the current name was found. The user can then make a new recording of the
     * name that they can add to the database.
     */
    private void updateMissingNames() {
        if(!_currentName.getMissingNames().isEmpty()) {
            _missingNamesLabel.setText(MISSING_MSG + _currentName.getMissingNames());
        } else {
            _missingNamesLabel.setText("");
        }
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
    }

    /**
     * This method should be called when the playing of the name audio to the
     * user has ended. Stops the progress bar, and brings the displays the play
     * button again.
     */
    private void endAudio(){
        _playBar.progressProperty().unbind();
        _playBar.setProgress(0);
        _playBtn.toFront();
    }

    /**
     * Updates the compare and keep buttons depending on whether the user has
     * made a recording that they can compare or keep.
     */
    private void updateUserComparisonButtons() {
        if(_practiseListModel.userHasRecorded()) {
            _compareBtn.setDisable(false);
            _keepBtn.setDisable(false);
        } else {
            _compareBtn.setDisable(true);
            _keepBtn.setDisable(true);
        }
    }

    /**
     * Help button opens the user manual pdf from the current working directory.
     * @param actionEvent
     */
    public void helpButtonAction(ActionEvent actionEvent) {
        URL documentURL = getClass().getResource(USER_MANUAL);
        new DocumentLoader(USER_MANUAL).loadDocument();
    }
}
