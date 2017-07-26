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

/**
 * Created by User on 13.07.17.
 */
public class EditDialogController{
    @FXML public TextField txtFolName;
    @FXML public Button btnOk;
    @FXML public Button btnCancel;
    public Label lblDirectory;

    private String directory;


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
            directory = directory+"\\"+txtFolName.getText();
            if (new File(directory).mkdir())
                {actionCancel(actionEvent);}
            else {DialogManager.showInfoDialog("Info dialog", "Write new ");}
        }
    }



    public void actionCancel(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }



}
