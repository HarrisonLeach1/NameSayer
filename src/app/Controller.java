package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ListView<String> listView;

    IDataModel dataModel = new DataModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listView.getItems().addAll(dataModel.loadData());
    }



}
