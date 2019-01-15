package pl.edu.wat.jfk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.wat.jfk.controllers.PrimaryViewController;
import pl.edu.wat.jfk.services.JarService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        PrimaryViewController.init(new JarService(), primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/views/primaryView.fxml"));
        primaryStage.setTitle("Jar Modifier");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
