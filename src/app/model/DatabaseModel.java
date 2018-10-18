package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The DatabaseModel singleton object represents the database in which practise and user recordings
 * are loaded in from and saved to.
 *
 * The displayable databases are returned in Tree View form and List View form, which allows
 * them to be easily presented to the user.
 */
public class DatabaseModel implements IDatabaseModel {
    public static final File USER_DATABASE = new File("./userRecordings/");
    private static DatabaseModel _instance;
    private File _database = new File("./names/");
	private HashMap<String, Name> _databaseTable;
	private List<String> _nameStrings;

	private DatabaseModel() {
    	_databaseTable = createNameTable(_database);
	}


	/**
	 * Returns the singleton instance of the DatabaseModel, used for loading
	 * in the recording databases.
	 * @return instance of DatabaseModel
	 */
	public static DatabaseModel getInstance() {
		if (_instance == null) {
			_instance = new DatabaseModel();
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
		return checkTree.getTreeRoot(root, createNameTable(_database));
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
		HashMap<String, Name> nameTable = createNameTable(_database);

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
	 * Given a non-empty folder, all files are converted to NameVersion objects
	 * and are stored in their respective Name which is an encapsulated collection of versions.
	 * Each Name as a value in the HashMap and are keyed by a string corresponding to their name.
	 *
	 * A HashMap was used to allow for time-efficient search and retrieval.
	 *
	 * @return the HashMap containing the names keyed by a string
	 */
	private HashMap<String, Name> createNameTable(File databaseFolder) {
		HashMap<String, Name> nameTable = new HashMap<>();
		_nameStrings = new ArrayList<>();

		if(!databaseFolder.exists()){
			return new HashMap<>();
		}

		File[] files  = databaseFolder.listFiles();

		// loop through files to add recordings to table
		for (File file : files) {
			if (file.isFile()) {

				// define the path the recording will use to find it's wav file
				// if the folder has spaces they must be replaced "\ " to be interpreted by a bash process
				String fileName = databaseFolder.getName().replaceAll(" ","\\\\ ") + "/" + file.getName();

				// create a name version object for the recording file
				NameVersion nameVersion = new NameVersion(fileName);

				// update the list of all names
				_nameStrings.add(nameVersion.getShortName());

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
	 * Deletes the temporary directory for storing modified audio files if
	 * it exists.
	 */
	public void deleteTempRecordings() {
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

	/**
	 * Changes the directory of names that this Database refers to, and
	 * resets the name table in which the names are stored.
	 */
	public void setDatabase(File database) {
        _database = database;
        _databaseTable = createNameTable(_database);
	}

	/**
	 * Returns the name of the database that this data model represents.
	 *
	 * The name of the database is determined initially by the name of the
	 * directory which it references.
	 * @return name of the database
	 */
	public String getDatabaseName() {
		return _database.getName();
	}

	/**
	 * Returns the number of name objects contained within this database.
	 *
	 * Note that this does not refer to the number of files within the folder,
	 * but rather the number of unique names.
	 * @return number of Names in the database
	 */
	public int getDatabaseNameCount() {
		return _databaseTable.keySet().size();
	}

	/**
	 * Returns displayable strings of all the names in the currently selected database.
	 * @return
	 */
	public List<String> getNameStrings() {
		return _nameStrings;
	}
}
