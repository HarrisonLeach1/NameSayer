package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;

public class DataModel implements IDataModel{
    private static final File DATABASE = new File("./names");

    private final ObservableList<String> _nameList = FXCollections.observableArrayList();

    public ObservableList<String> loadData() {

    	File[] files = DATABASE.listFiles();
    	//If DATABASE does not exist, then listFiles() returns null.

    	for (File file : files) {
    	    if (file.isFile()) {
    	        _nameList.add(file.getName()); // need to add name parsing here
    	    }
    	}
        return _nameList;
    }
}
