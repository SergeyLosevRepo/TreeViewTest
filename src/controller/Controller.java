package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.DialogManager;
import java.io.File;
import java.io.IOException;
import java.util.Optional;


public class Controller {

    @FXML  public TreeView vwTree;

    public ContextMenu menuContext;
    public MenuItem miCreate;
    private Stage stage;
    private File[] root = File.listRoots();
    private TreeItem<String> vwRoot = new TreeItem<>("My");
    private EditDialogController editDialogController;
    private Stage editDialogStage;
    private Parent fxmlEdit;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private String directory;

    // слушатель на событие Expanded TreeItem.
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getDirectory(){
        return directory;
    }

    public void initialize() throws IOException {
        // Добавляем исходные каталоги в TreeView и скрываем корневой каталог.
        for (int i = 0; i < root.length; i++) {
            if (root[i].exists()){TreeItem<String> t = new TreeItem<>(root[i].getAbsolutePath());
            t.expandedProperty().addListener(listener);
            t.getChildren().add(new TreeItem<String>(""));
            vwRoot.getChildren().add(t);}
        }
        vwTree.setRoot(vwRoot);
        vwTree.setShowRoot(false);
        initLoader();
    }

    // Условия возникновения ContextMenu в главном окне.
    public void passContext(ContextMenuEvent contextMenuEvent) {
        if (vwTree.getSelectionModel().isEmpty()) menuContext.hide(); else{
        TreeItem<String> item = (TreeItem<String>) vwTree.getSelectionModel().getSelectedItem();
        if (item.getValue().toString().equals("<folder is empty>")) menuContext.hide();
        if (item.getChildren().isEmpty()) menuContext.hide();}
    }

    // Создание каталога внутри выбранного Item
    public void preessCreateFolder(ActionEvent actionEvent) {
        TreeItem<String> item = (TreeItem<String>) vwTree.getSelectionModel().getSelectedItem();
        ListBuilder listBuilder = new ListBuilder(item, vwTree);
        this.directory = listBuilder.getDirectory();
        editDialogController.txtFolName.clear();
        editDialogController.lblDirectory.setText(directory);
        editDialogController.setDirectory(directory);
        showDialog();
        TreeItem<String> folNameItem = new TreeItem<>(editDialogController.getFolName());
        TreeItem<String> tEmpty = new TreeItem<>("");
        folNameItem.getChildren().add(tEmpty);
        folNameItem.expandedProperty().addListener(listener);
        if (item.getChildren().get(0).getValue().toString().equals("<folder is empty>")) item.getChildren().clear();
        item.getChildren().add(folNameItem);
    }

    // Удаление каталога выбранного в TreeItem
    public void pressDeleteFolder(ActionEvent actionEvent) {
        TreeItem<String> item = (TreeItem<String>) vwTree.getSelectionModel().getSelectedItem();
        ListBuilder listBuilder = new ListBuilder(item, vwTree);
        this.directory = listBuilder.getDirectory();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete folder");
        alert.setContentText("Do you want delete:  " + directory);
        alert.setHeaderText("");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            File file = new File(directory);
            delete(file);
            System.out.println(file.exists());
            if (file.exists()) {DialogManager.showErorDialog("Delete Error", "The directory can not be deleted."); return;}
            item.getParent().getChildren().remove(item);
        } else {
            return;
        }
    }

    // Удаление директории
    public void delete(File file) {
        if (!file.exists()) return;

        if (file.isDirectory()) {
            for (File f : file.listFiles())
                    delete(f);
                file.delete();
        } else {
            file.delete();
        }
    }

    //Инициализация окна ввода имени
    private void showDialog() {
        if (editDialogStage == null) {
            editDialogStage = new Stage();
            editDialogStage.setTitle("Create folder");
            editDialogStage.setMinHeight(150);
            editDialogStage.setMinWidth(300);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(fxmlEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(stage);
        }

        editDialogStage.showAndWait();
    }

    //Загрузка данных второго окна.
    private void initLoader() {
        try {
            fxmlLoader.setLocation(getClass().getResource("../view/edit.fxml"));
            fxmlEdit = fxmlLoader.load();
            editDialogController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
