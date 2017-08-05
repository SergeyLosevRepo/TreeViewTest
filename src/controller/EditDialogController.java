package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DialogManager;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EditDialogController{
    @FXML public TextField txtFolName;
    @FXML public Button btnOk;
    @FXML public Button btnCancel;

    public Label lblDirectory;
    private String directory;
    private Boolean createFolder = false;
    private Stage editDialogStage;

    public void setEditDialogStage(Stage editDialogStage) {
        this.editDialogStage = editDialogStage;
    }

    public void setCreateFolder(Boolean createFolder) {
        this.createFolder = createFolder;
    }

    public Boolean isCreateFolder() {
        return createFolder;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFolName() {
        return txtFolName.getText();
    }

    public void actionOk(ActionEvent actionEvent) {
        if (txtFolName.getText().isEmpty()) {
            DialogManager.showErorDialog("eror","Folder name is empty.");}
        else{
            if (editDialogStage.getTitle().equals("Create folder")) {
                String direct = directory + "\\" + txtFolName.getText();
                if (createFolder = new File(direct).mkdir()) {
                    actionCancel(actionEvent);
                } else {
                    DialogManager.showInfoDialog("Info dialog", "The folder can not be created");
                    direct = directory;
                }
            }
            if (editDialogStage.getTitle().equals("Rename folder")) {
                String direct = directory.substring(0, directory.lastIndexOf("\\"));
                System.out.println(directory);
                direct = direct + "\\" + txtFolName.getText();
                System.out.println(direct);
                if (createFolder = new File(directory).renameTo(new File(direct))) {
                    actionCancel(actionEvent);
                } else {
                    DialogManager.showInfoDialog("Info dialog", "The folder can not be created");
                    direct = directory;
                }

            }
        }
    }

    public void actionCancel(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

}
