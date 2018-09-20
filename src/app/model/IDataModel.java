package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import sun.reflect.generics.tree.Tree;

public interface IDataModel {
    /**
     * Initialises the model by loading in the Names from the database folder.
     */
    CheckBoxTreeItem<Name> loadDatabase();
}
