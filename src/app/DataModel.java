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
		HashMap<String, ArrayList<Name>> nameTable = getNameTable();

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
	private HashMap<String, ArrayList<Name>> getNameTable() {
		HashMap<Name, ArrayList<Name>> nameTable = new HashMap<>();

		File[] files = DATABASE.listFiles();

		// loop through files to add recordings to table
 		for (File file : files) {
			if (file.isFile()) {

				// retrieve full file name and create a name object from it
				Name name = new Name(file.getName());

				// if other versions of the same name exist, add to the list
				if (nameTable.containsKey(name.getShortName())) {
					ArrayList<Name> currentList = nameTable.get(name);
					currentList.add(fileName);
				} else { // otherwise create a new key for the name
					ArrayList<Name> version = new ArrayList<Name>(Arrays.asList(name));
					nameTable.put(name.getShortName(), version);
				}
			}
		}

		return nameTable;
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
