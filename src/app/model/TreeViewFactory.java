package app.model;


import javafx.scene.control.TreeItem;
import java.io.File;
import java.util.*;

/**
 * A TreeViewFactory creates a TreeView from a database of files.
 *
 * All files in the database folder should be named in the format:
 *
 * se206_dd-MM-yyyy_HH-mm-ss_Name.wav
 *
 * e.g. se206_2-5-2018_15-23-50_Mason.wav
 *
 * It has the Factory method addBranch which should be overridden by subclasses to decide which
 * TreeItems the TreeView should contain.
 */
public abstract class TreeViewFactory {

    /**
     * Given a Name object and a TreeItem, the Name is wrapped in a TreeItem object and added as a
     * child of the given TreeItem parent in the TreeView.
     *
     * This is the factory method, subclasses decide which type of TreeItems the TreeView should have.
     *
     * @param child
     * @param parent
     * @return the TreeItem that has been added as a child to the parent
     */
    protected abstract TreeItem<Name> addBranch(Name child, TreeItem<Name> parent);

    /**
     * Given a TreeItem and a path to database containing Name files, children are added to the
     * TreeItem such that the TreeItem can be used in a TreeView to represent the Name heirarchy
     * of the database.
     *
     * @param root
     * @param database
     * @return the TreeItem to be used as root for the TreeView
     */
    public TreeItem<Name> getTreeRoot(TreeItem<Name> root , String database) {
        root.setExpanded(true);

        // hashmap for storing all different versions (values) associated with a specific name (key)
        HashMap<String, ArrayList<Name>> nameTable = getNameTable(database);

        // loop through each name in table and build tree
        for (String key : nameTable.keySet()) {

            // get all versions with the name
            ArrayList<Name> versions = nameTable.get(key);

            // if multiple versions of the name exist, add children
            if (versions.size() > 1) {

                // creates a placeholder node that bridges to all subversions of the name
                Name bridgeName = new Name();
                bridgeName.setVersionName(key);
                TreeItem<Name> bridgeNode = addBranch(bridgeName,root);

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

        // Sort children alphabetically
        Collections.sort(root.getChildren(), Comparator.comparing((TreeItem<Name> o) -> o.getValue().toString()));

        return root;
    }

    /**
     * Given a the path to a non-empty folder, all files are converted to Name objects
     * and are stored in a HashMap under the key corresponding to their short name.
     * This allows for time-efficient detection of duplicates.
     *
     * @param database
     * @return the HashMap containing the
     */
    protected HashMap<String, ArrayList<Name>> getNameTable(String database) {
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
