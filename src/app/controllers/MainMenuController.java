package app.controllers;

import app.model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.CheckTreeView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static app.model.DataModel.USER_DATABASE;

/**
 * A MainMenuController holds the responsibility of receiving input events
 * from the user at the main menu and then translating them into actions on the
 * IDataModel.
 * The IDataModel then passes information back to the MainMenuController
 * to update the view.
 */
public class MainMenuController implements Initializable {

    @FXML private Pane _dataPane, _recPane;
    @FXML private Button _viewDataBtn,_viewRecBtn,_testMicBtn;
    @FXML private CheckListView<Name> _dataList;
    @FXML private ListView<Name> _selectedList;
    @FXML private TreeView<Name> _recList;


    private IDataModel dataModel = new DataModel();


    /**
     * Initially the database of recordings is loaded in from the model,
     * and displayed in the TreeView of the main menu view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _dataList.getItems().addAll(dataModel.loadDatabaseList());
        _selectedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
            _recList.setRoot(dataModel.loadUserDatabaseTree());
            _recList.setShowRoot(false);
            _recPane.toFront();
        } else if(event.getSource() == _testMicBtn){
            Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/TestScene.fxml"));
            Scene playerScene = new Scene(playerParent);
            Stage window = new Stage();

            // when test mic scene is closed, delete the test audio file
            window.setScene(playerScene);
            window.setOnCloseRequest(event1 -> {
                File file = new File(USER_DATABASE + "_test.wav");
                if (file.exists()){
                    file.delete();
                }
            });

            window.initModality(Modality.APPLICATION_MODAL);
            window.showAndWait();
        }
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
        ObservableList<Name> itemsToDelete = _selectedList.getSelectionModel().getSelectedItems();
        _selectedList.getItems().removeAll(itemsToDelete);
    }

    /**
     * All items in the selected list are removed from the selected list.
     */
    public void removeAllButtonPressed() {
        _selectedList.getItems().removeAll(_selectedList.getItems());
    }

    /**
     * The order of the Name items in the selected list of Names is shuffled randomly
     */
    public void randomiseButtonPressed() {
        Collections.shuffle(_selectedList.getItems());
    }


    /**
     * Loads in all Name objects in the selected list, passes it to the next view
     * and controller, and switches scenes.
     * @param event
     * @throws IOException
     */
    public void handleStartAction(ActionEvent event) throws IOException {
        // if no items are selected, do not switch scenes.
        if(_selectedList.getItems().size() == 0){ return; }

        // load in the new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app/views/PlayScene.fxml"));
        Parent playerParent = loader.load();

        // pass selected items to the next controller
        PlaySceneController controller = loader.getController();
        controller.initModel(new PractiseListModel(_selectedList.getItems()));

        // switch scenes
        Scene playerScene = new Scene(playerParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }

    /**
     * Plays the currently selected user recording in the list of user recordings.
     */
    public void playButtonPressed() {
        TreeItem<Name> selectedItem = _recList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Name currentUserRecording = _recList.getSelectionModel().getSelectedItem().getValue();

            if(currentUserRecording != null) {
                currentUserRecording.playRecording();
            }
        }

    }
}

