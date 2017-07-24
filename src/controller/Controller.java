package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    @FXML  public TreeView vwTree;

    private Stage stage;
    private File[] root = File.listRoots();
    private TreeItem<String> tList;
    private TreeItem<String> vwRoot = new TreeItem<>("My computer");
    private ObservableList<Integer> b = FXCollections.observableArrayList();
    private ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            BooleanProperty bb = (BooleanProperty) observable;
            TreeItem t = (TreeItem) bb.getBean();
            if (t.isExpanded()) {
                t.getChildren().clear();
                tList = t;
                b.add(1);
            }else if (!t.isExpanded()){
                t.getChildren().clear();
                t.getChildren().add(new TreeItem<>(""));
            }

        }
    };


    public void initialize() throws IOException {
        ArrayList<TreeItem<String>> list = new ArrayList<>();
        for (int i = 0; i < root.length; i++) {
            TreeItem<String> t = new TreeItem<>(root[i].getAbsolutePath());
            if (root[i].list() != null){
            t.getChildren().addAll(createItemList(root[i]));} else t.getChildren().add(new TreeItem<String>("<folder is empty>"));
            list.add(t);
        }

        b.addListener( new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                TreeItem<String> treeItem = tList.getParent();
                String s = tList.getValue().toString();
                s = "\\" + s;
                for (int i = 0; i < vwTree.getTreeItemLevel(tList)-2; i++) {
                    s = "\\" + treeItem.getValue().toString() + s;
                    treeItem = treeItem.getParent();
                }
                s = s.substring(1, s.length());
                s = treeItem.getValue().toString() + s;
                if (new File(s).list() != null) {
                    File[] files = new File(s).listFiles();

                    for (int i = 0; i < files.length; i++) {
                        TreeItem<String> item = new TreeItem<String>(files[i].getName());
                        item.expandedProperty().addListener(listener);
                        if (files[i].isDirectory()) {
                            item.getChildren().add(new TreeItem<String>(""));
                        } else item.setGraphic(new ImageView(new Image("view/file.png")));
                        tList.getChildren().add(item);

                    }
                } else tList.getChildren().add(new TreeItem<String>("<folder is empty>"));
            }
        });

        vwRoot.getChildren().addAll(list);

        vwTree.setRoot(vwRoot);

        vwTree.setShowRoot(false);

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private ArrayList<TreeItem<String>> createItemList(File entryFile){
        File[] files = entryFile.listFiles();
        ArrayList<TreeItem<String>> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            TreeItem<String> t = new TreeItem<>(files[i].getName());
            t.expandedProperty().addListener(listener);
            if (files[i].isDirectory()) t.getChildren().add(new TreeItem<>("aaa")); else t.setGraphic(new ImageView(new Image("view/file.png")));
            list.add(t);
        }
        return list;
    }
}
