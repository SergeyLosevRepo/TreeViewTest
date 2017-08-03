package controller;

import Interfaces.ICreateItem;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;


public class ListBuilder implements ICreateItem {
    private TreeItem<String> tList;
    private ChangeListener<Boolean> listener;
    private TreeView<String> vwTree;

    public ListBuilder(TreeItem<String> tList, TreeView<String> vwTree) {
        this.tList = tList;
        this.vwTree = vwTree;
    }


    public ListBuilder(TreeItem<String> tList, ChangeListener<Boolean> listener, TreeView<String> vwTree) {
        this.tList = tList;
        this.listener = listener;
        this.vwTree = vwTree;
    }
    // Создание объекта в TreeView по указанной дирректории.
    @Override
    public void createItem(File file) {
        TreeItem<String> item = new TreeItem<String>(file.getName());
        item.expandedProperty().addListener(listener);
        if (file.isDirectory()) {
            item.getChildren().add(new TreeItem<String>(""));
        } else item.setGraphic(new ImageView(new Image("view/file.png")));
        tList.getChildren().add(item);
    }
    //Получение дирректории из TreeView для выбранного объекта.
    public String getDirectory(){
        TreeItem<String> treeItem = tList.getParent();
        String directory = tList.getValue().toString();
        directory = "\\" + directory;
        for (int i = 0; i < vwTree.getTreeItemLevel(tList)-2; i++) {
            directory = "\\" + treeItem.getValue().toString() + directory;
            treeItem = treeItem.getParent();
        }
        directory = directory.substring(1, directory.length());
        if (!treeItem.getValue().toString().equals("My")) directory = treeItem.getValue().toString() + directory;
        return directory;
    }
    //Добавление объектов в TreeView . Сюда обращается Expand слушатель.
    @Override
    public void addItems() {
        tList.getChildren().clear();
        String directory = getDirectory();
        if (new File(directory).list() != null && new File(directory).list().length != 0) {
            File[] files = new File(directory).listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].exists())createItem(files[i]);
            }
        } else {
            TreeItem<String> tEmpty = new TreeItem<>("<folder is empty>");
            tEmpty.setGraphic(new ImageView(new Image("view/empty-folder.png")));
            tList.getChildren().add(tEmpty);
        }
    }
    // Очистка заданного TreeItem.
    @Override
    public void clearFolder() {
        tList.getChildren().clear();
        tList.getChildren().add(new TreeItem<>(""));
    }

}
