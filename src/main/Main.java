package main;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../view/sample.fxml"));

        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Data Manager");

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

