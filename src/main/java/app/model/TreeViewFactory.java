package app.model;


import app.model.processing.SingleName;
import app.model.processing.SingleNameVersion;
import javafx.scene.control.TreeItem;

import java.util.*;

/**
 * A TreeViewFactory creates a TreeView from a database of files.
 *
 * All files in the database folder should be named in the format:
 *
 * Creator_dd-MM-yyyy_HH-mm-ss_Name.wav
 *
 * e.g. se206_2-5-2018_15-23-50_Mason.wav
 *
 * It has the Factory method addBranch which should be overridden by subclasses to decide which
 * TreeItems the TreeView should contain.
 */
public abstract class TreeViewFactory {

    /**
     * Given a SingleNameVersion object and a TreeItem, the SingleNameVersion is wrapped in a TreeItem object and added as a
     * child of the given TreeItem parent in the TreeView.
     *
     * This is the factory method, subclasses decide which type of TreeItems the TreeView should have.
     *
     * @param child
     * @param parent
     * @return the TreeItem that has been added as a child to the parent
     */
    protected abstract TreeItem<SingleNameVersion> addBranch(SingleNameVersion child, TreeItem<SingleNameVersion> parent);

    /**
     * Given a TreeItem and a path to database containing SingleNameVersion files, children are added to the
     * TreeItem such that the TreeItem can be used in a TreeView to represent the SingleNameVersion heirarchy
     * of the database.
     *
     * @param root
     * @param database
     * @return the TreeItem to be used as root for the TreeView
     */
    public TreeItem<SingleNameVersion> getTreeRoot(TreeItem<SingleNameVersion> root , HashMap<String, SingleName> database) {
        root.setExpanded(true);

        // loop through each name in table and build tree
        for (String key : database.keySet()) {

            // name that stores all versions
            SingleName name = database.get(key);

            // if multiple versions of the name exist, add children
            if (name.size() > 1) {

                // creates a placeholder node that bridges to all versions of the name
                SingleNameVersion bridgeName = new SingleNameVersion();
                bridgeName.setDisplayName(key);
                TreeItem<SingleNameVersion> bridgeNode = addBranch(bridgeName,root);

                // add versions of this name to be children of the placeholder node
                for (int i = 0; i < name.size(); i++) {
                    SingleNameVersion version = name.get(i);
                    addBranch(version, bridgeNode);
                }

            } else {
                SingleNameVersion singleName = name.get(0);
                singleName.setDisplayName(key);
                addBranch(singleName,root);
            }
        }

        // Sort children alphabetically
        Collections.sort(root.getChildren(), Comparator.comparing((TreeItem<SingleNameVersion> o) -> o.getValue().toString()));

        return root;
    }
}
