package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.*;

public class DataModel implements IDataModel{
    public static final String DATABASE = "./names/";
    public static final String USER_DATABASE = "./userRecordings/";
    public static final DataModel INSTANCE = new DataModel();
	private final HashMap<String, Name> _databaseTable;

	private DataModel() {
    	_databaseTable = createNameTable(DATABASE);
	}

	/**
	 * Returns the singleton instance of the DataModel, used for loading
	 * in the recording databases.
	 * @return instance of DataModel
	 */
	public static DataModel getInstance() {
    	return INSTANCE;
	}

    /**
     * Creates a TreeItem that contains all recordings in the database
     * as descendants. Uses the TreeViewFactory.
     */
	public TreeItem<NameVersion> loadDatabaseTree(){
		TreeViewFactory checkTree = new CheckTreeViewFactory();
		CheckBoxTreeItem<NameVersion> root = new CheckBoxTreeItem<>();
		return checkTree.getTreeRoot(root, createNameTable(DATABASE));
	}

    /**
     * Creates a CheckBox TreeItem that contains all recordings in the user
     * recordings database as descendants. Uses the CheckTreeViewFactory.
     */
	public TreeItem<NameVersion> loadUserDatabaseTree(){
		TreeViewFactory checkTree = new RegularTreeViewFactory();
		TreeItem<NameVersion> root = new TreeItem<>();
		return checkTree.getTreeRoot(root, createNameTable(DATABASE));
	}

	/**
	 * Creates a List of Names containing only one version of each name.
	 * @return ArrayList
	 */
	public List<Name> loadDatabaseList() {
		HashMap<String, Name> nameTable = createNameTable(DATABASE);

		List<Name> nameList = new ArrayList<>();

		// loop through each base name
		for (String key : nameTable.keySet()) {

			// randomly select name from nameTable
			Name selectedName = nameTable.get(key);

			nameList.add(selectedName);
		}

		// sort alphabetically
		Collections.sort(nameList, Comparator.comparing((Name o) -> o.toString()));

		return nameList;
	}

	/**
	 * Given a the path to a non-empty folder, all files are converted to NameVersion objects
	 * and are stored in their respective Name which is an encapsulated collection of versions.
	 * Each Name as a value in the HashMap and are keyed by a string corresponding to their name.
	 *
	 * A HashMap was used to allow for time-efficient search and retrieval.
	 *
	 * @return the HashMap containing the names keyed by a string
	 */
	private HashMap<String, Name> createNameTable(String database) {
		HashMap<String, Name> nameTable = new HashMap<>();

		File databaseFolder = new File(database);
		if(!databaseFolder.exists()){
			return new HashMap<>();
		}

		File[] files  = databaseFolder.listFiles();

		// loop through files to add recordings to table
		for (File file : files) {
			if (file.isFile()) {

				// retrieve full file name and create a NameVersion object from it
				NameVersion nameVersion = new NameVersion(database + file.getName());

				// if other versions of the same nameVersion exist, add it to the name
				if (nameTable.containsKey(nameVersion.getShortName().toLowerCase())) {
					Name currentName = nameTable.get(nameVersion.getShortName().toLowerCase());
					currentName.add(nameVersion);
				} else { // otherwise create a new Name, add version to the Name
					Name name = new Name(nameVersion.getShortName());
					name.add(nameVersion);
					nameTable.put(nameVersion.getShortName().toLowerCase(), name);

					// initialises the good version to be used as the recording for this name
					name.selectGoodVersion();
				}
			}
		}

		return nameTable;
	}


	public HashMap<String, Name> getDatabaseTable() {
		return _databaseTable;
	}
}
