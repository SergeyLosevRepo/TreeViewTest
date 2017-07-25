package controller;

import Interfaces.ICreateItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
    private TreeItem<String> vwRoot = new TreeItem<>("My");
    private ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            BooleanProperty bb = (BooleanProperty) observable;
            TreeItem tItem = (TreeItem) bb.getBean();
            ListBuilder item = new ListBuilder(tItem, listener, vwTree);
            if (tItem.isExpanded()) {
                tItem.getChildren().clear();
                TreeItem<String> itLoad = new TreeItem<String>("<folder is load>");
                itLoad.setGraphic(new ImageView(new Image("view/folder-refresh.png")));
                tItem.getChildren().add(itLoad);
                Service process = new Service() {
                    @Override
                    protected Task createTask() {
                        return new Task() {
                            @Override
                            protected Void call() throws Exception {

                                Thread.sleep(2000);
                                return null;
                            }
                        };
                    }
                };

                process.setOnSucceeded( e -> {
                    item.addItems();
                });

                process.start();
            }else if (!tItem.isExpanded()){
                item.clearFolder();
            }
        }
    };

    public void initialize() throws IOException {
        for (int i = 0; i < root.length; i++) {
            TreeItem<String> t = new TreeItem<>(root[i].getAbsolutePath());
            t.expandedProperty().addListener(listener);
            t.getChildren().add(new TreeItem<String>(""));
            vwRoot.getChildren().add(t);
        }

        vwTree.setRoot(vwRoot);
        vwTree.setShowRoot(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
