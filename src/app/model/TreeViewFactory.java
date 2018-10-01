package app.model;


import javafx.scene.control.TreeItem;

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
    public TreeItem<Name> getTreeRoot(TreeItem<Name> root , HashMap<String, ArrayList<Name>> database) {
        root.setExpanded(true);

        // loop through each name in table and build tree
        for (String key : database.keySet()) {

            // get all versions with the name
            ArrayList<Name> versions = database.get(key);

            // if multiple versions of the name exist, add children
            if (versions.size() > 1) {

                // creates a placeholder node that bridges to all subversions of the name
                Name bridgeName = new Name();
                bridgeName.setDisplayName(key);
                TreeItem<Name> bridgeNode = addBranch(bridgeName,root);

                // add all children with the name under this node
                for (Name version : versions) {
                    addBranch(version, bridgeNode);
                }

            } else {
                Name singleName = versions.get(0);
                singleName.setDisplayName(key);
                addBranch(singleName,root);
            }
        }

        // Sort children alphabetically
        Collections.sort(root.getChildren(), Comparator.comparing((TreeItem<Name> o) -> o.getValue().toString()));

        return root;
    }
}
