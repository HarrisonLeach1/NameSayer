package app.model;

import javafx.scene.control.TreeItem;

/**
 * A RegularTreeViewFactory is a TreeViewFactory that creates a TreeView from
 * a database of files, where each TreeItem in the TreeView is a TreeItem.
 */

public class RegularTreeViewFactory extends TreeViewFactory {

    @Override
    protected TreeItem<NameVersion> addBranch(NameVersion child, TreeItem<NameVersion> parent) {
        TreeItem<NameVersion> item = new TreeItem<>(child);
        item.setExpanded(false);
        parent.getChildren().add(item);
        return item;
    }
}
