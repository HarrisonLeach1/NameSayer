package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import sun.reflect.generics.tree.Tree;

public interface IDataModel {

    /**
     * Loads the database of recordings as a tree. The root is returned,
     * all user recordings are descendants of the root. All names children of
     * the root. If there is more than one version of a name, those version
     * will be children of that name.
     *
     * @return the TreeItem root of the TreeView
     */
    TreeItem<Name> loadDatabase();

    /**
     * Loads the database of user recordings as a tree. The root is returned,
     * all user recordings are descendants of the root. All names children of
     * the root. If there is more than one version of a name, those version
     * will be children of that name.
     *
     * @return the TreeItem root of the TreeView
     */
    TreeItem<Name> loadUserDatabase();

}
