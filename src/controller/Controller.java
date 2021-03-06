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
                TreeItem<String> tItemChil = (TreeItem<String>) tItem.getChildren().get(0);
                if (tItem.getChildren().size() == 1 && tItemChil.getValue().equals("")) {
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

                    process.setOnSucceeded(e -> {
                        item.addItems();
                    });

                    process.start();
                }

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
        editDialogController.lblDirectory.setText(directory);
        editDialogController.setDirectory(directory);
        showDialog("Create folder");
        if (editDialogController.isCreateFolder()){
            TreeItem<String> folNameItem = new TreeItem<>(editDialogController.getFolName());
            TreeItem<String> tNon = new TreeItem<>("");
            folNameItem.getChildren().add(tNon);
            folNameItem.expandedProperty().addListener(listener);
            if (item.getChildren().get(0).getValue().toString().equals("<folder is empty>")) item.getChildren().clear();
            item.getChildren().add(folNameItem);
        }
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
            if (file.exists()) {DialogManager.showErorDialog("Delete Error", "The directory can not be deleted."); return;}
            TreeItem<String> tItemParent = item.getParent();
            item.getParent().getChildren().remove(item);
            if (tItemParent.getChildren().size() == 0){
                TreeItem<String> tEmpty = new TreeItem<>("<folder is empty>");
                tEmpty.setGraphic(new ImageView(new Image("view/empty-folder.png")));
                tItemParent.getChildren().add(tEmpty);}
        } else {
            return;
        }
    }

    // Удаление директории
    public void delete(File file) {
        if (!file.exists()) return;

        if (file.isDirectory() && file.list() != null) {
            for (File f : file.listFiles())
                    delete(f);
                file.delete();
        } else {
            file.delete();
        }
    }

    //Инициализация окна ввода имени
    private void showDialog(String s) {
        if (editDialogStage == null){
            editDialogStage = new Stage();
            editDialogStage.setTitle(s);
            editDialogStage.setMinHeight(150);
            editDialogStage.setMinWidth(300);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(fxmlEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(stage);
            editDialogStage.getIcons().add(new Image("view/folder.png"));
            editDialogController.setEditDialogStage(editDialogStage);
            editDialogStage.showAndWait();}
        else{
            editDialogController.setCreateFolder(false);
            editDialogStage.setTitle(s);
            editDialogController.txtFolName.clear();
            editDialogStage.showAndWait();}
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

    public void pressRename(ActionEvent actionEvent) {
        TreeItem<String> item = (TreeItem<String>) vwTree.getSelectionModel().getSelectedItem();
        ListBuilder listBuilder = new ListBuilder(item, vwTree);
        this.directory = listBuilder.getDirectory();
        editDialogController.setEditDialogStage(editDialogStage);
        editDialogController.setDirectory(directory);
        editDialogController.lblDirectory.setText(directory);
        showDialog("Rename folder");
        if (editDialogController.isCreateFolder()) item.setValue(editDialogController.getFolName());

    }
}
