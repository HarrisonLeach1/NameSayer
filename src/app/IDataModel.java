package app;

import javafx.collections.ObservableList;

public interface IDataModel {
    /**
     * Initialises the model by loading in the Names from the database folder.
     */
    ObservableList<String> loadData();
}
