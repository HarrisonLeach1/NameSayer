package app.controllers;

import app.model.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.textfield.TextFields;

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
 * DataModel.
 *
 * The DataModel then passes information back to the MainMenuController
 * to update the view.
 */
public class MainMenuController implements Initializable, DataModelListener {
    private static final String STREAK_SCENE = "/app/views/StreakScene.fxml";
    private static final String CONFIRM_SCENE = "/app/views/ConfirmScene.fxml";
    private static final String ERROR_SCENE = "/app/views/ErrorScene.fxml";
    private static final String TEST_SCENE = "/app/views/TestScene.fxml";
    private static final String SAVE_PLAYLIST_SCENE = "/app/views/SavePlaylistScene.fxml";
    private static final String LOADING_SCENE = "/app/views/LoadingScene.fxml";

    @FXML private SplitPane _mainPane;
    @FXML private Pane _dataPane, _recPane, _searchPane, _startPane;
    @FXML private Button _viewDataBtn,_viewRecBtn,_testMicBtn,_searchMenuBtn;
    @FXML private CheckListView<Name> _dataList;
    @FXML private ListView<Practisable> _selectedList;
    @FXML private ListView<ConcatenatedName> _playList;
    @FXML private TreeView<NameVersion> _recList;
    @FXML private TextField _searchBox;
    @FXML private Label _fileNameLabel, _streakCounter, _levelCounter, _databaseLabel, _nameCountLabel;
    @FXML private ProgressBar _levelProgress;
    private File _selectedFile;
    private ConfirmSceneController _confirmationController;

    /**
     * Initially the database of recordings is loaded in from the model,
     * and displayed in the TreeView of the main menu view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _startPane.toFront();
        _dataList.getItems().addAll(DataModel.getInstance().loadDatabaseList());

        _streakCounter.setText(String.valueOf(DataModel.getInstance().getDailyStreak()));

        _selectedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _playList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        _databaseLabel.setText(DataModel.getInstance().getDatabaseName());
        _nameCountLabel.setText(String.valueOf(DataModel.getInstance().getDatabaseNameCount()));

        setupPlaylist();

        setupSearchBox();

        DataModel.getInstance().addListener(this);
    }

    /**
     * Initialises the search box to allow for autocompletion when the user
     * begins typing a name. Initially the autocomplete contains all database names,
     * then changes based on the users search. When the user enters a space or hyphen
     * the autocomplete return to all database names.
     */
    private void setupSearchBox() {
        TextFields.bindAutoCompletion(_searchBox, DataModel.getInstance().getNameStrings());

        _searchBox.textProperty().addListener((observable, oldValue, newValue) -> {

            // if the user types a hyphen or space, reset the autocomplete to all names
            if (newValue.endsWith(" ") || newValue.endsWith("-")) {
                List<String> autoCompleteList = new ArrayList<>();
                for(String name : DataModel.getInstance().getNameStrings()) {
                    autoCompleteList.add(newValue + name);
                }
                TextFields.bindAutoCompletion(_searchBox, autoCompleteList);
            }
        });
    }


    /**
     * When the start button is pressed, the user is displayed the search pane.
     * The user is also notified of their daily streak progress.
     */
    public void handleStartUpAction(){
        _startPane.toBack();
        openStreakWindow();
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
            DataModel.getInstance().setDatabase(selectedDirectory);

            // change GUI labels
            _databaseLabel.setText(DataModel.getInstance().getDatabaseName());
            _nameCountLabel.setText(String.valueOf(DataModel.getInstance().getDatabaseNameCount()));

            _dataList.getItems().clear();
            _dataList.getItems().addAll(DataModel.getInstance().loadDatabaseList());
        }
    }

    /**
     * When the find and play button is pressed the searched name is retrieved
     * and the user is moved to the play scene. If searched name does not exist
     * the user is asked if they want to continue with the missing names.
     * @param event
     * @throws IOException
     */
    public void playSearchPressed(ActionEvent event) throws IOException {
        if (_searchBox.getText().trim().isEmpty()) {
            loadErrorMessage("ERROR: Search is empty");
            return;
        }
        Task<List<ConcatenatedName>> loadWorker = DataModel.getInstance().loadSingleNameWorker(_searchBox.getText());

        loadWorker.setOnSucceeded(e -> {
            String missingNames = DataModel.getInstance().compileMissingNames(loadWorker.getValue());
            if(!missingNames.isEmpty()) { // if the name contains missing get user confirmation
                loadConfirmMessage("Could not find the following name(s): \n\n" + missingNames);
                if(_confirmationController.saidYes()) { // if they said yes continue practise with the missing names
                    moveToPlayScene(new ArrayList<>(loadWorker.getValue()), event);
                }
            } else { // otherwise, move on without asking
                moveToPlayScene(new ArrayList<>(loadWorker.getValue()), event);
            }
        });

        new Thread(loadWorker).start();
    }

    /**
     * Adds the string that is currently typed in the search box to be added to
     * the playlist.
     * @param event
     */
    public void addToPlaylistPressed(ActionEvent event) {
        if (_searchBox.getText().trim().isEmpty()) {
            loadErrorMessage("ERROR: Search is empty");
            return;
        }

        Task<List<ConcatenatedName>> loadWorker = DataModel.getInstance().loadSingleNameWorker(_searchBox.getText());

        // when finished update the list view
        loadWorker.setOnSucceeded(e -> {
            String missingNames = DataModel.getInstance().compileMissingNames(loadWorker.getValue());
            if(!missingNames.isEmpty()) {
                loadConfirmMessage("Could not find the following name(s): \n\n" + missingNames);
                if(_confirmationController.saidYes()) { // if they said yes continue practise with the missing names
                    _playList.getItems().addAll(new ArrayList<>(loadWorker.getValue()));
                    _searchBox.clear();
                }
            } else {
                _playList.getItems().addAll(new ArrayList<>(loadWorker.getValue()));
                _searchBox.clear();
            }
        });
        new Thread(loadWorker).start();
    }

    /**
     * Opens a file chooser which allows the user to upload the playlist they wish
     * to practise.
     * @param event
     * @throws IOException
     */
    public void chooseFilePressed(ActionEvent event) throws IOException {
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
     * @throws IOException
     */
    public void playFilePressed(ActionEvent event) throws IOException {
        if (_playList.getItems().size() == 0) { // check if list is empty
            loadErrorMessage("ERROR: List is empty");
            return;
        }

        // check if there are any missing names. If so, ask the user if they want to continue.
        String missingNames = DataModel.getInstance().compileMissingNames(_playList.getItems());
        if (!missingNames.isEmpty()){
            loadConfirmMessage("Could not find the following name(s): \n\n" + missingNames);
            if(_confirmationController.saidYes()) { // If their decision is yes, move to the play scene
                moveToPlayScene(new ArrayList<>(_playList.getItems()),event);
            }
        } else { // otherwise, allow user to practise.
            moveToPlayScene(new ArrayList<>(_playList.getItems()),event);
        }
    }

    /**
     * Given a file which represents the user playlist of names to practise, updates the
     * previewList with the names. If all names are found in the database, the playlist
     * field is loaded with names to practise.
     * @param selectedFile
     * @throws FileNotFoundException
     */
    private void loadFile(File selectedFile) throws IOException {
        _fileNameLabel.setText("  " + selectedFile.getName());
        _selectedFile = selectedFile;

        // create a load worker for loading in the names in the file
        Task<List<ConcatenatedName>> loadWorker = DataModel.getInstance().loadFileWorker(selectedFile);

        // load in the new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(LOADING_SCENE));
        Parent playerParent = null;
        try {
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass the task to be loaded to the controller
        LoadingController controller = loader.getController();
        controller.showTaskLoading(loadWorker);

        // when finished update the list view and close the loader
        loadWorker.setOnSucceeded(e -> {
            _playList.getItems().addAll(loadWorker.getValue());
            controller.cancelLoading();
        });

        openScene(playerParent);
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
        _fileNameLabel.setText("  No file selected");
    }

    /**
     * Saves the playlist of ConcatenatedNames that is currently listed in the listview
     */
    public void savePlayListPressed(ActionEvent event) {
        if (_playList.getItems().size() == 0) { // check if list is empty
            loadErrorMessage("ERROR: List is empty");
            return;
        }
        // load in the new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(SAVE_PLAYLIST_SCENE));

        Parent playerParent = null;
        try {
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass selected items to the next controller
        SavePlaylistController controller = loader.getController();
        controller.setPlayList(_playList.getItems());

        openScene(playerParent);
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
        if(_selectedList.getItems().size() == 0){ return; }
        moveToPlayScene(new ArrayList<>(_selectedList.getItems()), event);
    }

    /**
     * Plays the currently selected user recording in the list of user recordings.
     * Executes a on a new thread to avoid GUI unresponsiveness.
     */
    public void playUserRecordingPressed() {
        if (_recList.getSelectionModel().getSelectedItem() != null) {
            Task player = playWorker();
            new Thread(player).start();
        }
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
     * Handles any user input event related to the switching tabs.
     * @param event
     * @throws IOException
     */
    public void handleMenuAction(ActionEvent event) throws IOException {
        if(event.getSource() == _viewDataBtn){
            _dataPane.toFront();

        } else if(event.getSource() == _viewRecBtn){
            _recList.setRoot(DataModel.getInstance().loadUserDatabaseTree());
            _recList.setShowRoot(false);
            _recPane.toFront();

        } else if(event.getSource() == _searchMenuBtn){
            _searchPane.toFront();

        } else if(event.getSource() == _testMicBtn){
            openScene(TEST_SCENE);
        }
    }

    /**
     * Setup the playlist such that it displays when a ConcatenatedName object,
     * contains missing names.
     */
    private void setupPlaylist() {
        _playList.setCellFactory(lv -> new ListCell<ConcatenatedName>() {
            @Override
            protected void updateItem(ConcatenatedName c, boolean empty) {
                super.updateItem(c, empty);
                // if empty, ignore
                if (empty) {
                    setText(null);
                    setStyle("");

                } else {
                    setText(c.toString());

                    // If the ConcatenatedName object contains missing names, update
                    // the cell accordingly.
                    if (!c.getMissingNames().equals("")) {
                        setStyle("-fx-background-color: rgba(255,0,0,0.5)");
                        setTooltip( new Tooltip("Missing Name(s): " + c.getMissingNames()));
                    } else {
                        setStyle("");
                        setTooltip( new Tooltip("All Names Found!"));
                    }
                }
            }
        });
    }

    /**
     * Given a practise list, redirects the user to the play scene to practise the list of names.
     * @param list
     * @param event
     * @throws IOException
     */
    private void moveToPlayScene(List<Practisable> list , ActionEvent event) {
        // load in the new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app/views/PlayScene.fxml"));
        Parent playerParent = null;
        try {
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass selected items to the next controller
        PlaySceneController controller = loader.getController();
        controller.initModel(new PractiseListModel(new ArrayList<>(list)));

        // switch scenes
        Scene playerScene = new Scene(playerParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }

    /**
     * Given a message, displays an error pop-up to the user displaying the
     * message to user indicating what they have done wrong.
     * @param message
     */
    private void loadErrorMessage(String message) {
        // load in the new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(ERROR_SCENE));

        Parent playerParent = null;
        try {
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass selected items to the next controller
        ErrorSceneController controller = loader.getController();
        controller.setMessage(message);

        openScene(playerParent);
    }

    /**
     * Given a message, displays an confirm action pop-up to the user displaying the
     * message to the user and asking if they want to continue with their actions.
     * @param message
     */
    private void loadConfirmMessage(String message) {
        // load in the new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(CONFIRM_SCENE));

        Parent playerParent = null;
        try {
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pass selected items to the next controller
        _confirmationController = loader.getController();
        _confirmationController.setMessage(message);

        openScene(playerParent);
    }

    /**
     * Displays the streak window to the user to notify them of their daily streak
     * progress.
     */
    private void openStreakWindow() {
        openScene(STREAK_SCENE);
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
     * Given a path to a scene file, this scene is displayed to the user in a new window
     * @param scenePath
     */
    private void openScene(String scenePath) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(scenePath));

        Parent playerParent = null;
        try {
            playerParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        openScene(playerParent);
    }

    /**
     * Given a playerParent object, opens a new scene for this object in a new window.
     * @param playerParent
     */
    private void openScene(Parent playerParent) {
        // create new scene and window
        Scene playerScene = new Scene(playerParent);
        Stage window = new Stage();

        // display to the user
        window.setScene(playerScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
    }

}

