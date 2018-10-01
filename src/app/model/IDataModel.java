package app.model;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * An IDataModel object represents the database in which practise and user recordings
 * are to be stored. The displayable databases are returned in Tree View form
 * which allows them to be easily presentable to the user.
 */
public interface IDataModel {

    /**
     * Loads the database of recordings as a tree. The root is returned,
     * all user recordings are descendants of the root. All names children of
     * the root. If there is more than one version of a name, those version
     * will be children of that name.
     *
     * @return the TreeItem root of the TreeView
     */
    TreeItem<Name> loadDatabaseTree();

    /**
     * Loads the database of user recordings as a tree. The root is returned,
     * all user recordings are descendants of the root. All names children of
     * the root. If there is more than one version of a name, those version
     * will be children of that name.
     *
     * @return the TreeItem root of the TreeView
     */
    TreeItem<Name> loadUserDatabaseTree();

    List<Name> loadDatabaseList();

}
