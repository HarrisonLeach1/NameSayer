package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.controlsfx.control.CheckTreeView;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private CheckTreeView<String> _dataList;

    IDataModel dataModel = new DataModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
         _dataList.setRoot(dataModel.getTreeRoot());
         _dataList.setShowRoot(false);
     }

    private TreeItem<String> addBranch(String name, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<>(name);
        parent.getChildren().add(item);
        return item;
    }

}
