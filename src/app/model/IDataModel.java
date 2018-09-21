package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import sun.reflect.generics.tree.Tree;

public interface IDataModel {

    TreeItem<Name> loadDatabase();

    TreeItem<Name> loadUserDatabase();

}
