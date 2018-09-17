package app.model;

import javafx.scene.control.TreeItem;

public interface IDataModel {
    /**
     * Initialises the model by loading in the Names from the database folder.
     */
    TreeItem<String> getTreeRoot();
}
