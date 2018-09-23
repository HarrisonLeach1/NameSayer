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
import org.controlsfx.control.CheckTreeView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static app.model.DataModel.USER_DATABASE;

public class MainMenuController implements Initializable {

    @FXML private Pane _dataPane, _recPane;
    @FXML private Button _viewDataBtn,_viewRecBtn,_testMicBtn;
    @FXML private CheckTreeView<Name> _dataList;
    @FXML private ListView<Name> _selectedList;
    @FXML private TreeView<Name> _recList;


    private IDataModel dataModel = new DataModel();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _dataList.setRoot(dataModel.loadDatabase());
        _dataList.setShowRoot(false);
        _selectedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void handleMenuAction(ActionEvent event) throws IOException {
        if(event.getSource() == _viewDataBtn){
            _dataPane.toFront();
        } else if(event.getSource() == _viewRecBtn){
            _recList.setRoot(dataModel.loadUserDatabase());
            _recList.setShowRoot(false);
            _recPane.toFront();
        } else if(event.getSource() == _testMicBtn){
            Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/TestScene.fxml"));
            Scene playerScene = new Scene(playerParent);
            Stage window = new Stage();
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

    public void handleStartAction(ActionEvent event) throws IOException {
        // if no items are selected, do not switch scenes.
        if(_selectedList.getItems().size() == 0){ return; }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app/views/PlayScene.fxml"));
        Parent playerParent = loader.load();

        PlaySceneController controller = loader.getController();
        controller.initModel(new PractiseListModel(_selectedList.getItems()));

        Scene playerScene = new Scene(playerParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(playerScene);
    }


    /**
     * All checked items in the CheckTreeView are added to the selected list of names
     * to be practised by the user.
     */
    public void addButtonPressed() {
        List<TreeItem<Name>> checkedNames = _dataList.getCheckModel().getCheckedItems();
        List<Name> selectedItems = _selectedList.getItems();
        for (TreeItem<Name> name : checkedNames) {
            if (name.getChildren().size() < 2) { // only add if name is not a leaf
                selectedItems.add(name.getValue());
            }
        }
        _dataList.getCheckModel().clearChecks(); // clear items checked after they have been added
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

