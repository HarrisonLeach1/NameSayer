package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

/**
 * A CheckTreeViewFactory is a TreeViewFactory that creates a TreeView from
 * a database of files, where each TreeItem in the TreeView is a CheckBoxTreeItem.
 */
public class CheckTreeViewFactory extends TreeViewFactory {

    @Override
    protected CheckBoxTreeItem<Name> addBranch(Name child, TreeItem<Name> parent) {
        CheckBoxTreeItem<Name> item = new CheckBoxTreeItem<>(child);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }
}
