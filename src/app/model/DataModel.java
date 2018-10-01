package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataModel implements IDataModel{
    public static final String DATABASE = "./names/";
    public static final String USER_DATABASE = "./userRecordings/";

    /**
     * Creates a TreeItem that contains all recordings in the database
     * as descendants. Uses the TreeViewFactory.
     */
	public TreeItem<Name> loadDatabaseTree(){
		TreeViewFactory checkTree = new CheckTreeViewFactory();
		CheckBoxTreeItem<Name> root = new CheckBoxTreeItem<>();
		return checkTree.getTreeRoot(root, getNameTable(DATABASE));
	}

    /**
     * Creates a CheckBox TreeItem that contains all recordings in the user
     * recordings database as descendants. Uses the CheckTreeViewFactory.
     */
	public TreeItem<Name> loadUserDatabaseTree(){
		TreeViewFactory checkTree = new RegularTreeViewFactory();
		TreeItem<Name> root = new TreeItem<>();
		return checkTree.getTreeRoot(root, getNameTable(USER_DATABASE));
	}

	/**
	 * Given a the path to a non-empty folder, all files are converted to Name objects
	 * and are stored in a HashMap under the key corresponding to their short name.
	 * This allows for time-efficient detection of duplicates.
	 *
	 * @return the HashMap containing the
	 */
	private HashMap<String, ArrayList<Name>> getNameTable(String database) {
		HashMap<String, ArrayList<Name>> nameTable = new HashMap<>();

		File databaseFolder = new File(database);
		if(!databaseFolder.exists()){
			return new HashMap<>();
		}

		File[] files  = databaseFolder.listFiles();

		// loop through files to add recordings to table
		for (File file : files) {
			if (file.isFile()) {

				// retrieve full file name and create a name object from it
				Name name = new Name(database + file.getName());

				// if other versions of the same name exist, add to the list
				if (nameTable.containsKey(name.getShortName())) {
					ArrayList<Name> currentList = nameTable.get(name.getShortName());
					currentList.add(name);
				} else { // otherwise create a new key for the name
					ArrayList<Name> version = new ArrayList<>(Arrays.asList(name));
					nameTable.put(name.getShortName(), version);
				}
			}
		}

		return nameTable;
	}


}
