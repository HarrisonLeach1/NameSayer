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
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML private Pane data_pane, rec_pane;
    @FXML private Button view_data_btn,view_rec_btn,test_mic_btn;
    @FXML private CheckTreeView<Name> _dataList;
    @FXML private ListView<Name> _selectedList;
    @FXML private TreeView<Name> rec_list;


    private IDataModel dataModel = new DataModel();
    private IUserRecordingsModel userRecordingsModel = new UserRecordingsModel();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _dataList.setRoot(dataModel.loadDatabase());
        _dataList.setShowRoot(false);
        _dataList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void handleMenuAction(ActionEvent event) throws IOException {
        if(event.getSource() == view_data_btn){
            data_pane.toFront();
        } else if(event.getSource() == view_rec_btn){
            rec_list.setRoot(dataModel.loadUserDatabase());
            rec_list.setShowRoot(false);
            rec_pane.toFront();
        } else if(event.getSource() == test_mic_btn){
            Parent playerParent = FXMLLoader.load(getClass().getResource("/app/views/RecordingScene.fxml"));
            Scene playerScene = new Scene(playerParent);

            Stage window = new Stage();
            window.setScene(playerScene);
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
     * When the add button is pressed all checked names in the check tree view are added to the
     * selected names list.
     */
    public void addButtonPressed() {
        List<TreeItem<Name>> checkedNames = _dataList.getCheckModel().getCheckedItems();
        for (TreeItem<Name> name : checkedNames) {
            if (name.getChildren().size() < 2) { // if a node is not a leaf, do not add it
                _selectedList.getItems().add(name.getValue());
            }
        }
        _dataList.getCheckModel().clearChecks(); // clear items checked after they have been added
    }

    /**
     * When the remove button is pressed all selected items in the selected list are removed
     * from being practised.
     */
    public void removeButtonPressed() {
        ObservableList<Name> itemsToDelete = _selectedList.getSelectionModel().getSelectedItems();
        _selectedList.getItems().removeAll(itemsToDelete);
    }

    /**
     * When the randomise button the order of the items in the selected list are shuffled.
     */
    public void randomiseButtonPressed() {
        Collections.shuffle(_selectedList.getItems());
    }

    public void playButtonPressed() {
        Name currentUserRecording = rec_list.getSelectionModel().getSelectedItem().getValue();
        if(currentUserRecording != null) {
            currentUserRecording.playRecording();
        }

    }
}

