package controller;

import Interfaces.ICreateItem;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * Created by 802222 on 23.07.2017.
 */
public class ListBuilder implements ICreateItem {
    private TreeItem<String> tList;
    private ChangeListener<Boolean> listener;
    private TreeView<String> vwTree;



    public ListBuilder(TreeItem<String> tList, ChangeListener<Boolean> listener, TreeView<String> vwTree) {
        this.tList = tList;
        this.listener = listener;
        this.vwTree = vwTree;
    }

    @Override
    public void createItem(File file) {
        TreeItem<String> item = new TreeItem<String>(file.getName());
        item.expandedProperty().addListener(listener);
        if (file.isDirectory()) {
            item.getChildren().add(new TreeItem<String>(""));
        } else item.setGraphic(new ImageView(new Image("view/file.png")));
        tList.getChildren().add(item);
    }

    @Override
    public synchronized void addItems() {
        tList.getChildren().clear();
        TreeItem<String> treeItem = tList.getParent();
        String s = tList.getValue().toString();
        s = "\\" + s;
        for (int i = 0; i < vwTree.getTreeItemLevel(tList)-2; i++) {
            s = "\\" + treeItem.getValue().toString() + s;
            treeItem = treeItem.getParent();
        }
        s = s.substring(1, s.length());
        if (!treeItem.getValue().toString().equals("My")) s = treeItem.getValue().toString() + s;
        if (new File(s).list() != null) {
            File[] files = new File(s).listFiles();
            for (int i = 0; i < files.length; i++) {
                createItem(files[i]);
            }
        } else tList.getChildren().add(new TreeItem<String>("<folder is empty>"));
    }

    @Override
    public void clearFolder() {
        tList.getChildren().clear();
        tList.getChildren().add(new TreeItem<>(""));
    }

}
