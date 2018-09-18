package app;

import javafx.scene.control.CheckBoxTreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataModel implements IDataModel{
    private static final File DATABASE = new File("./names");

	@Override
	public CheckBoxTreeItem<String> getTreeRoot() {
		File[] files = DATABASE.listFiles();

		CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>();
		root.setExpanded(true);
		// hashmap for storing all different versions (values) asoociated with a specific name (key)
		HashMap<String, ArrayList<String>> nameTable = getNameTable();

		// loop through each name in table and build tree
		for (String key : nameTable.keySet()) {

			// get all versions with the name
			ArrayList<String> versions = nameTable.get(key);

			// create branch for the name
			CheckBoxTreeItem<String> name = addBranch(key,root);

			// if multiple versions of the name exist, add children
			if (versions.size() > 1) {
				for (String version : versions) {
					addBranch(version, name);
				}
			}
		}
		return root;
	}

	/**
	 * Returns a Hashmap for storing all different versions (values) asoociated with a
	 * specific name (key)
	 */
	private HashMap<String, ArrayList<String>> getNameTable() {
		HashMap<String, ArrayList<String>> nameTable = new HashMap<>();

		File[] files = DATABASE.listFiles();

		// loop through files to add recordings to table
 		for (File file : files) {
			if (file.isFile()) {

				// retrieve full file name and presentable name
				String fileName = file.getName();
				String name = parseName(fileName);

				// if other versions of the same name exist, add to the list
				if (nameTable.containsKey(name)) {
					ArrayList<String> currentList = nameTable.get(name);
					currentList.add(fileName);
				} else { // otherwise create a new key for the name
					ArrayList<String> version = new ArrayList<String>(Arrays.asList(fileName));
					nameTable.put(name, version);
				}
			}
		}

		return nameTable;
	}

	/**
	 * Returns the basename of the recording excluding the creation date, time and file extension.
	 * @param fileName
	 */
	private String parseName(String fileName) {
		String dateTimeRemoved = fileName.substring(fileName.lastIndexOf('_') + 1);
		return dateTimeRemoved.split("\\.")[0];
	}

	/**
	 * Takes a String and adds it to the tree by making it a CheckBoxTreeItem under the specified parent.
	 * @param name
	 * @param parent
	 */
	private CheckBoxTreeItem<String> addBranch(String name, CheckBoxTreeItem<String> parent) {
		CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(name);
		item.setExpanded(true);
		parent.getChildren().add(item);
		return item;
	}

}
