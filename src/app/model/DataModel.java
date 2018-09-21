package app.model;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataModel implements IDataModel{
    public static final String DATABASE = "./names/";
    public static final String USER_DATABASE = "./userRecordings/";

	public TreeItem<Name> loadDatabase(){
		TreeViewFactory checkTree = new CheckTreeViewFactory();
		CheckBoxTreeItem<Name> root = new CheckBoxTreeItem<>();
		return checkTree.getTreeRoot(root, DATABASE);
	}

	public TreeItem<Name> loadUserDatabase(){
		TreeViewFactory checkTree = new RegularTreeViewFactory();
		TreeItem<Name> root = new TreeItem<>();
		return checkTree.getTreeRoot(root, USER_DATABASE);
	}


}
