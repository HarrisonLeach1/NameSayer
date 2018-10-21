package app.model;

import javafx.concurrent.Task;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * The DatabaseModel singleton object represents the database in which practise and user
 * recordings are loaded in from and saved to.
 *
 * The displayable databases are returned in Tree View form and List form, which allows
 * them to be easily presented to the user.
 * For a recording file to be recognised as a valid name recording in this database, it
 * must have the following file name format:
 *
 * se206_dd-MM-yyyy_HH-mm-ss_Name.wav
 * e.g. se206_2-5-2018_15-23-50_Mason.wav
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
	 * A HashMap is used to allow for time-efficient search and retrieval.
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

				// try create a name version object for the file, if the following exceptions are thrown
				// then the file is not a valid practise recording file. i.e. incorrect file name format
				NameVersion nameVersion = null;
				try {
					nameVersion = new NameVersion(fileName);
				} catch (ParseException | IndexOutOfBoundsException e) {
					// if an exception has been thrown then the file is invalid, do not create a NameVersion for it
					continue;
				}

				// if other versions of the same nameVersion exist, add it to the name
				if (nameTable.containsKey(nameVersion.getShortName().toLowerCase())) {
					Name currentName = nameTable.get(nameVersion.getShortName().toLowerCase());
					currentName.add(nameVersion);
				} else { // otherwise create a new Name, add version to the Name
					Name name = new Name(nameVersion.getShortName());
					name.add(nameVersion);
					nameTable.put(nameVersion.getShortName().toLowerCase(), name);

					// update the list of all names
					_nameStrings.add(nameVersion.getShortName());

					// initialises the good version to be used as the recording for this name
					name.selectGoodVersion();
				}
			}
		}

		return nameTable;
	}

	/**
	 * Given a single name string, returns a task which returns a list of concatenated names
	 * containing this name. The loadSingleNameTask executes the loading of the name on
	 * a background thread to avoid GUI unresponsiveness.
	 * @param name
	 * @return
	 */
	public Task loadSingleNameTask(String name) {
		return new Task() {
			@Override
			protected List<ConcatenatedName> call() throws Exception {
				// load name through data app.model
				List<ConcatenatedName> list = new ArrayList<>(Arrays.asList(new ConcatenatedName(name, _databaseTable)));

				// compile missing names into a string to display to the user if needed
				compileMissingNames(list);
				return list;
			}
		};
	}

	/**
	 * Given a file, returns a task which returns a list of ConcatenatedName objects where
	 * each line of the file is converted to a ConcatenatedName object in the list.
	 * The loadFileTask executes the parsing of the playlist file on a background thread
	 * to avoid GUI unresponsiveness.
	 * @param playlistFile
	 * @return
	 */
	public Task loadFileTask(File playlistFile) {
		return new Task() {
			@Override
			protected List<ConcatenatedName> call() throws FileNotFoundException {
				Scanner input = new Scanner(playlistFile);
				List<ConcatenatedName> nameList = new ArrayList<>();

				// load in each line of the text file, and use each string to create a new Name object
				while (input.hasNextLine()) {
					String inputString = input.nextLine();

					// if the concatenation of a name is interrupted ask for the cause
					ConcatenatedName concatenatedName = null;
					try {
						concatenatedName = new ConcatenatedName(inputString, _databaseTable);
					} catch (InterruptedException e) {
						// if the exception was caused by the user cancelling, it is not an error
						if(isCancelled()){
							break;
						}
						// otherwise, the interruption was unexpected and the user should be notified
						e.printStackTrace();
					}

					nameList.add(concatenatedName);
				}
				compileMissingNames(nameList);
				return nameList;
			}
		};
	}

	/**
	 * Returns a displayable string which contains all the names that are not
	 * in this database in each of the given Concatenated Name objects.
	 * @param list
	 */
	public String compileMissingNames(List<ConcatenatedName> list) {
		String missingNames = "";
		// loop through all names in the list
		for(ConcatenatedName name : list) {
			String missing = name.getMissingNames();

			// if some names are missing, update the _missingNames field
			if (!missing.isEmpty()) {
				missingNames += missing +"\n";
			}
		}
		return missingNames;
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
	 * Returns the name of the database that this data app.model represents.
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
