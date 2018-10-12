package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The DataModel singleton object represents the database in which practise and user recordings
 * are loaded in from and saved to.
 *
 * The displayable databases are returned in Tree View form and List View form, which allows
 * them to be easily presented to the user.
 */
public class DataModel implements IDataModel{
    public static final String DATABASE = "./names/";
    public static final String USER_DATABASE = "./names/";
    private static DataModel _instance;
	private final HashMap<String, Name> _databaseTable;
	private List<DataModelListener> _listeners;
	private User _user;

	private DataModel() {
		_user = new User();
    	_databaseTable = createNameTable(DATABASE);
		_listeners = new ArrayList<>();
	}

	/**
	 * Returns the singleton instance of the DataModel, used for loading
	 * in the recording databases.
	 * @return instance of DataModel
	 */
	public static DataModel getInstance() {
    	if (_instance == null) {
			_instance = new DataModel();
		}
		return _instance;
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
		return checkTree.getTreeRoot(root, createNameTable(USER_DATABASE));
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

	public void addListener(DataModelListener listener) {
		_listeners.add(listener);
		listener.notifyProgress(_user.getUserXP());
	}

	/**
	 * Updates the experience of the user object
	 */
	public void updateUserXP() {
		_user.updateUserXP();
		int experience = _user.getUserXP();
			for(DataModelListener l : _listeners) {
				l.notifyProgress(experience);
			}
	}

	public int getDailyStreak() {
		return _user.getDailyStreak();
	}

	public HashMap<String, Name> getDatabaseTable() {
		return _databaseTable;
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

	/**
	 * Given a single name string, returns a list of list of concatenated names containing
	 * this name.
	 * @param name
	 * @return
	 */
	public List<ConcatenatedName> loadSingleNameToList(String name) {
		return new ArrayList<>(Arrays.asList(createConcatenatedName(name)));
	}

	/**
	 * Given a file, returns a list of ConcatenatedName objects in which each line of the
	 * file is converted to a ConcatenatedName object in the list.
	 * @param playlistFile
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<ConcatenatedName> loadFileToList(File playlistFile) throws FileNotFoundException {
		Scanner input = new Scanner(playlistFile);
		List<ConcatenatedName> nameList = new ArrayList<>();

		// load in each line of the text file, and use each string to create a new Name object
		while (input.hasNextLine()) {
			String inputString = input.nextLine();

			ConcatenatedName concatenatedName = createConcatenatedName(inputString);

			nameList.add(concatenatedName);
		}

		return nameList;
	}

	/**
	 * Given an input string, returns a Concatenated Name object from the string.
	 * @param inputString
	 * @return
	 */
	private ConcatenatedName createConcatenatedName(String inputString) {
		List<Name> notConcatenatedNames = new ArrayList<>();

		// replace all hyphens with spaces
		String splitString = inputString.replaceAll("-", " ");

		// parse strings into a list of strings
		List<String> stringList = new ArrayList<>(Arrays.asList(splitString.split(" ")));

		String missingNames = "";

		for (String str : stringList) {
			if (_databaseTable.containsKey(str.toLowerCase())) {
				notConcatenatedNames.add(_databaseTable.get(str.toLowerCase()));
			} else {
				missingNames += str + " ";
			}
		}

		ConcatenatedName concatenatedName = new ConcatenatedName(notConcatenatedNames, inputString);

		if(!missingNames.isEmpty()) {
			concatenatedName.setMissingNames(missingNames);
		}

		return concatenatedName;
	}

	/**
	 * Converts a string of names into a list of name objects found the DataModel search table
	 * @param names the string of names
	 * @return list of Name objects
	 * @throws NameNotFoundException
	 */
	private List<Name> stringsToList(String names) throws NameNotFoundException {
		// replace all hyphens with spaces
		names = names.replaceAll("-"," ");

		// parse strings into a list of strings
		List<String> stringList = new ArrayList<>(Arrays.asList(names.split(" ")));

		List<Name> nameList = new ArrayList<>();

		// get the DataModel table which references the names with their associated strings
		HashMap<String, Name> searchTable = DataModel.getInstance().getDatabaseTable();

		// initialise the variable to store names that are not found
		String missingNames = "";

		// for each string, retrieve the Name object associated with the specific string key
		for (String str : stringList) {
			if (searchTable.containsKey(str.toLowerCase())) {
				nameList.add(searchTable.get(str.toLowerCase()));
			} else {
				missingNames += str + "\n";
			}
		}

		// if there are missing names in the string, notify by throwing an exception
		if (!missingNames.equals("")) {
			throw new NameNotFoundException(missingNames);
		}

		return nameList;
	}

	/**
	 * Deletes the temporary directory for storing modified audio files.
	 */
	public void deleteTempDirectory() {
		try {
			String cmd = "rm -rf " + ConcatenatedName.TEMP_FOLDER;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a list of concatenated names and a playlist name, creates a text
	 * file storing the strings of all the names.
	 * @param list
	 * @param fileName
	 */
	public void savePlaylist(List<ConcatenatedName> list, String fileName) {
		// replace all spaces with underscores
		fileName = fileName.replaceAll(" ","_") +".txt";
		File file = new File(fileName);

		try {
			file.createNewFile();

			FileWriter fw = new FileWriter(file);

			// for each name write it on a new line of the file
			for(ConcatenatedName name : list) {
				fw.write(name.toString() + "\r\n");
			}

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
