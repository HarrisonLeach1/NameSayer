package app.model;

import app.model.processing.SingleNameVersion;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

/**
 * A CheckTreeViewFactory is a TreeViewFactory that creates a TreeView from
 * a database of files, where each TreeItem in the TreeView is a CheckBoxTreeItem.
 */
public class CheckTreeViewFactory extends TreeViewFactory {

    @Override
    protected CheckBoxTreeItem<SingleNameVersion> addBranch(SingleNameVersion child, TreeItem<SingleNameVersion> parent) {
        CheckBoxTreeItem<SingleNameVersion> item = new CheckBoxTreeItem<>(child);
        item.setExpanded(false);
        parent.getChildren().add(item);
        return item;
    }
}
