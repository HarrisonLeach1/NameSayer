package app.controllers;

import app.model.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A MainMenuController holds the responsibility of receiving input events
 * from the user at the main menu and then translating them into actions on the
 * IDatabaseModel.
 *
 * The IDatabaseModel then passes information back to the MainMenuController
 * to update the view.
 */
public class MainMenuController implements Initializable, UserModelListener {
    private static final String TEST_SCENE = "/views/TestScene.fxml";
    private static final String SAVE_PLAYLIST_SCENE = "/views/SavePlaylistScene.fxml";
    private static final String LOADING_SCENE = "/views/LoadingScene.fxml";
    private static final String PLAY_SCENE = "/views/PlayScene.fxml";
    private static final int ERROR_SCENE_VALUE = 1;
    private static final int CONFIRM_SCENE_VALUE = 2;
    private static final int STREAK_SCENE_VALUE = 3;

    @FXML private Pane _dataPane, _recPane, _searchPane, _startPane;
    @FXML private Button _returnBtn, _viewDataBtn,_viewRecBtn,_testMicBtn,_searchMenuBtn;
    @FXML private CheckListView<Name> _dataList;
    @FXML private ListView<Practisable> _selectedList;
    @FXML private ListView<Practisable> _playList;
    @FXML private TreeView<NameVersion> _recList;
    @FXML private TextField _searchBox;
    @FXML private Label _fileNameLabel, _streakCounter, _levelCounter, _databaseLabel, _nameCountLabel;
    @FXML private ProgressBar _playingProgress,_levelProgress;

    private Task _player;
    private IDatabaseModel _databaseModel;
    private IUserModel _userModel;
    private static boolean start = true;

    /**
     * Initially the database of recordings is loaded in from the app.model,
     * and displayed in the TreeView of the main menu view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(start) {
            _startPane.toFront();
        }else{
            _startPane.toBack();
        }
        _selectedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _playList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        new HighlightedList(_playList);

        _dataList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                _selectedList.getItems().add(_dataList.getSelectionModel().getSelectedItem());
            }
        });

        _selectedList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                _selectedList.getItems().add(_dataList.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Initialises the IDatabaseModel and IUserModel to which this MainMenuController
     * communicates. This determines the information displayed and data saved.
     * @param databaseModel
     * @param userModel
     */
    public void setModel(IDatabaseModel databaseModel, IUserModel userModel) {
        _databaseModel = databaseModel;
        _userModel = userModel;

        new SearchField(_searchBox, _databaseModel);

        _dataList.getItems().addAll(_databaseModel.loadDatabaseList());
        _streakCounter.setText(String.valueOf(_userModel.getDailyStreak()));

        _databaseLabel.setText(_databaseModel.getDatabaseName());
        _nameCountLabel.setText(String.valueOf(_databaseModel.getDatabaseNameCount()));

        _userModel.addListener(this);
    }

    /**
     * Sets the playlist in the search pane with a list of Practisable
     * objects to be modified by the user.
     * @param list
     */
    public void setPlaylist(List<Practisable> list) {
        _playList.getItems().addAll(list);
    }

    /**
     * When the start button is pressed, the user is displayed the search pane.
     * The user is also notified of their daily streak progress.
     */
    public void handleStartUpAction(){
        _startPane.toBack();
        if (start) {
            new SceneLoader().loadStreakMessage(_userModel);
        }
    }

    /**
     * When the quit action is pressed the window and application are closed.
     * @param event
     */
    public void handleQuitAction(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * When the user wants to select a database of names to practise. They are
     * taken to a directory chooser window to select the directory. Once the directory
     * is selected the database is loaded in and the database GUI labels are updated.
     */
    public void handleSelectDatabaseAction(ActionEvent event) {
        // initialise directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Database");

        // open directory chooser
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(window);

        if (selectedDirectory != null) {
            // change database
            _databaseModel.setDatabase(selectedDirectory);

            // change GUI labels
            _databaseLabel.setText(_databaseModel.getDatabaseName());
            _nameCountLabel.setText(String.valueOf(_databaseModel.getDatabaseNameCount()));

            _dataList.getItems().clear();
            _dataList.getItems().addAll(_databaseModel.loadDatabaseList());
        }
    }

    /**
     * When the find and play button is pressed the searched name is retrieved
     * and the user is moved to the play scene. If searched name does not exist
     * the user is asked if they want to continue with the missing names.
     * @param event
     */
    public void playSearchPressed(ActionEvent event) {
        if (_searchBox.getText().trim().isEmpty()) {
            new SceneLoader("ERROR: Search is empty");
        } else {
            Task<List<Practisable>> loadWorker = _databaseModel.loadSingleNameTask(_searchBox.getText());

            loadWorker.setOnSucceeded(e -> {
                // use a PlaylistLoader to decide whether or not to go to the next scene
                new PlaylistLoader(loadWorker.getValue(), _databaseModel, _userModel).moveToPlayScene(event);
            });
            new Thread(loadWorker).start();
        }
    }

    /**
     * Adds the string that is currently typed in the search box to be added to
     * the playlist.
     * @param event
     */
    public void addToPlaylist(ActionEvent event) {
        if (_searchBox.getText().trim().isEmpty()) {
            new SceneLoader("ERROR: Search is empty");
        } else {
            Task<List<Practisable>> loadWorker = _databaseModel.loadSingleNameTask(_searchBox.getText());

            // when finished loading the name, update the list view
            loadWorker.setOnSucceeded(e -> {
                _playList.getItems().addAll(new ArrayList<>(loadWorker.getValue()));
                _searchBox.clear();
            });
            new Thread(loadWorker).start();
        }
    }

    /**
     * Opens a file chooser which allows the user to upload the playlist they wish
     * to practise.
     * @param event
     */
    public void chooseFilePressed(ActionEvent event) {
        // initialise file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PlayList");

        // only show text files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        // open file chooser
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            loadFile(selectedFile);
        }
    }

    /**
     * If a fully valid playlist has been loaded in by the user, moves to the play scene
     * to practise the playlist. Otherwise, the user is asked if they want to continue
     * with the playlist that contains missing names.
     * @param event
     */
    public void playFilePressed(ActionEvent event) {
        if (_playList.getItems().size() == 0) { // check if list is empty
            new SceneLoader().loadErrorMessage("ERROR: List is empty");

        } else { // otherwise, use a PlaylistLoader to decide whether or not to go to the next scene
            new PlaylistLoader(_playList.getItems(), _databaseModel, _userModel).moveToPlayScene(event);
        }
    }

    /**
     * Given a file which represents the user playlist of names to practise, updates the
     * previewList with the names. If all names are found in the database, the playlist
     * field is loaded with names to practise.
     * @param selectedFile
     */
    private void loadFile(File selectedFile) {
        _fileNameLabel.setText("  " + selectedFile.getName());

        // create a load worker for loading in the names in the file
        Task<List<ConcatenatedName>> loadWorker = _databaseModel.loadFileTask(selectedFile);

        // load in the new scene
        SceneLoader loader = new SceneLoader(LOADING_SCENE);

        // pass the task to be loaded to the controller
        LoadingController controller = loader.getController();
        controller.showTaskLoading(loadWorker);

        // when finished update the list view and close the loader
        loadWorker.setOnSucceeded(e -> {
            _playList.getItems().addAll(loadWorker.getValue());
            controller.cancelLoading();
        });

        loader.openScene();
    }

    /**
     * All checked items in the CheckListView are added to the selected list of names
     * to be practised by the user.
     */
    public void addButtonPressed() {
        // add all checked items to the selected list
        _selectedList.getItems().addAll(_dataList.getCheckModel().getCheckedItems());

        // clear items checked after they have been added
        _dataList.getCheckModel().clearChecks();
    }

    /**
     * All items in the CheckTreeView are added to the selected list of names
     * to be practised by the user
     */
    public void addAllButtonPressed() {
        _dataList.getCheckModel().checkAll();
        addButtonPressed();
    }


    /**
     * All selected items in the selected list are removed from the selected list.
     */
    public void removeButtonPressed() {
        ObservableList<Practisable> itemsToDelete = _selectedList.getSelectionModel().getSelectedItems();
        _selectedList.getItems().removeAll(itemsToDelete);
    }

    /**
     * All items in the selected list are removed from the selected list.
     */
    public void removeAllButtonPressed() {
        _selectedList.getItems().removeAll(_selectedList.getItems());
    }

    /**
     * All ConcatenatedName items in the playlist are removed
     */
    public void clearPlayListButtonPressed() {
        _playList.getItems().clear();
        _fileNameLabel.setText(" No file selected");
    }

    /**
     * Saves the playlist of ConcatenatedNames that is currently listed in the listview
     */
    public void savePlayListPressed(ActionEvent event) {
        if (_playList.getItems().size() == 0) { // check if list is empty
            new SceneLoader().loadErrorMessage("ERROR: List is empty");
            return;
        }
        // load in the new scene
        SceneLoader loader = new SceneLoader(SAVE_PLAYLIST_SCENE);

        // pass selected items to the next controller
        SavePlaylistController controller = loader.getController();
        controller.setPlayList(_playList.getItems());

        loader.openScene();
    }

    /**
     * The order of the ConcatenatedName items in the playlist is shuffled randomly
     */
    public void playListShuffleButtonPressed() {
        Collections.shuffle(_playList.getItems());
    }

    /**
     * The order of the NameVersion items in the selected list of Names is shuffled randomly
     */
    public void databaseShuffleButtonPressed() {
        Collections.shuffle(_selectedList.getItems());
    }


    /**
     * Loads in all NameVersion objects in the selected list, passes it to the next view
     * and controller, and switches scenes.
     * @param event
     * @throws IOException
     */
    public void handleStartAction(ActionEvent event) {
        // if no items are selected, do not switch scenes.
        if(_selectedList.getItems().size() != 0) {
            new PlaylistLoader(_selectedList.getItems(), _databaseModel, _userModel).moveToPlayScene(event);
        }
    }

    /**
     * Plays the currently selected user recording in the list of user recordings.
     * Executes a on a new thread to avoid unresponsiveness.
     */
    public void playUserRecordingPressed() {
        if (_recList.getSelectionModel().getSelectedItem() != null) {
            _player = playWorker();
            _player.setOnSucceeded( e -> stopProgress());
            _playingProgress.progressProperty().bind(_player.progressProperty());
            new Thread(_player).start();
        }
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
     * Handles any user input event related to the switching tabs.
     * @param event
     * @throws IOException
     */
    public void handleMenuAction(ActionEvent event) throws IOException {
        if(event.getSource() == _viewDataBtn){
            _dataPane.toFront();

        } else if(event.getSource() == _viewRecBtn){
            _recList.setRoot(_databaseModel.loadUserDatabaseTree());
            _recList.setShowRoot(false);
            _recPane.toFront();

        } else if(event.getSource() == _searchMenuBtn){
            _searchPane.toFront();

        } else if(event.getSource() == _testMicBtn){
            new SceneLoader(TEST_SCENE).openScene();

        } else if(event.getSource() == _returnBtn){
            start = false;
            _startPane.toFront();
        }
    }

    /**
     * Removes all currently selected names from the playlist. This can be a single name
     * or multiple names.
     * @param event
     */
    public void removeFromPlaylist(ActionEvent event) {
        _playList.getItems().removeAll(_playList.getSelectionModel().getSelectedItems());
    }

    /**
     * Sets start value to false so that the it doesn't show the
     * start screen and streaks when returning to the start screen
     */
    public static void setStartFalse(){
        start = false;
    }

    /**
     * Help button opens the user manual pdf from the current working directory.
     * @param actionEvent
     */
    public void helpButtonAction(ActionEvent actionEvent) {
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File("UserManual.pdf");
                if (myFile.exists()) {
                    Desktop.getDesktop().open(myFile);
                }else{
                    new SceneLoader().loadErrorMessage("ERROR: UserManual not found");
                }
            } catch (IOException ex) {
                new SceneLoader().loadErrorMessage("ERROR: Can't find application for opening PDF");
            }
        }
    }

    /**
     * Safely resets progress bar when playing has finished.
     */
    private void stopProgress(){
        _playingProgress.progressProperty().unbind();
        _playingProgress.setProgress(0);
        _player.cancel();
    }

    /**
     * Creates a new Task which allows the play functionality to be
     * executed on a new thread.
     */
    private Task playWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                // play user recording
                _recList.getSelectionModel().getSelectedItem().getValue().playRecording(1.0);
                return true;
            }
        };

    }
}