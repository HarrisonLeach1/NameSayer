package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

public class CheckTreeViewFactory extends TreeViewFactory {

    @Override
    protected CheckBoxTreeItem<Name> addBranch(Name child, TreeItem<Name> parent) {
        CheckBoxTreeItem<Name> item = new CheckBoxTreeItem<>(child);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }
}
