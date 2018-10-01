package app.model;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.*;

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
	 * Creates a List of Names containing only one version of each name.
	 * @return ArrayList
	 */
	public List<Name> loadDatabaseList() {
		HashMap<String, ArrayList<Name>> nameTable = getNameTable(DATABASE);

		List<Name> nameList = new ArrayList<Name>();

		// loop through each base name
		for (String key : nameTable.keySet()) {

			// randomly select name from nameTable
			Name selectedName = selectQualityVersion(nameTable.get(key));

			// set display name to short version
			selectedName.setDisplayName(selectedName.getShortName());

			nameList.add(selectedName);
		}

		// sort alphabetically
		Collections.sort(nameList, Comparator.comparing((Name o) -> o.toString()));

		return nameList;
	}

	/**
	 * Given a list, if there exists a good quality version in the list of a given name, one
	 * is chosen at and returned. Otherwise, a bad recording is returned.
	 * @param list
	 * @return Name that was selected
	 */
	private Name selectQualityVersion(ArrayList<Name> list) {
		// initialise random generator
		Random random = new Random();

		// loop through list of versions of names until a good quality version is found
		for(Name n: list) {
			//if(!n.isBadQuality) { // if good (not bad) quality, return the version
				return n;
			//}
		}
		// if no good quality version is found return any recording
		return list.get(0);

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
