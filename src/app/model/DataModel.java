package app.model;

import javafx.scene.control.CheckBoxTreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataModel implements IDataModel{
    public static final String DATABASE = "./names/";

	@Override
	public CheckBoxTreeItem<Name> loadDatabase() {
		CheckBoxTreeItem<Name> root = new CheckBoxTreeItem<>();
		root.setExpanded(true);
		// hashmap for storing all different versions (values) associated with a specific name (key)
		HashMap<String, ArrayList<Name>> nameTable = getNameTable();

		// loop through each name in table and build tree
		for (String key : nameTable.keySet()) {

			// get all versions with the name
			ArrayList<Name> versions = nameTable.get(key);

			// if multiple versions of the name exist, add children
			if (versions.size() > 1) {
				// creates a placeholder node that bridges to all subversions of the name
				Name bridgeName = new Name();
				bridgeName.setVersionName(key);
				CheckBoxTreeItem<Name> bridgeNode = addBranch(bridgeName,root);

				// add all children with the name under this node
				for (Name version : versions) {
					addBranch(version, bridgeNode);
				}

			} else {
				Name singleName = versions.get(0);
				singleName.setVersionName(key);
				addBranch(singleName,root);
			}
		}
		return root;
	}

	/**
	 * Returns a Hashmap for storing all different versions (values) asoociated with a
	 * specific name (key)
	 */
	private HashMap<String, ArrayList<Name>> getNameTable() {
		HashMap<String, ArrayList<Name>> nameTable = new HashMap<>();

		File[] files = new File(DATABASE).listFiles();

		// loop through files to add recordings to table
 		for (File file : files) {
			if (file.isFile()) {

				// retrieve full file name and create a name object from it
				Name name = new Name(DATABASE + file.getName());

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

	/**
	 * Takes a String and adds it to the tree by making it a CheckBoxTreeItem under the specified parent.
	 * @param child
	 * @param parent
	 */
	private CheckBoxTreeItem<Name> addBranch(Name child, CheckBoxTreeItem<Name> parent) {
		CheckBoxTreeItem<Name> item = new CheckBoxTreeItem<>(child);
		item.setExpanded(true);
		parent.getChildren().add(item);
		return item;
	}

}
