package app.model;

import javafx.scene.control.TreeItem;

/**
 * A data model represents the database in which practise and user recordings
 * are to be stored. The displayable databases are returned in Tree View form
 * which allows them to be easily presentable to the user.
 */
public interface IDataModel {

    TreeItem<Name> loadDatabase();

    TreeItem<Name> loadUserDatabase();

}
