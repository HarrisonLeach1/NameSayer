package app.model;

import javafx.scene.control.TreeItem;

public class RegularTreeViewFactory extends TreeViewFactory {

    @Override
    protected TreeItem<Name> addBranch(Name child, TreeItem<Name> parent) {
        TreeItem<Name> item = new TreeItem<>(child);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }
}
