package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.utils.UIUtils;

/**
 * Main
 * @author VStambolic
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources\\home.fxml"));
        primaryStage.setTitle("OpenPGP Crypto GUI");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("resources\\style.css").toExternalForm());
        UIUtils.setStage(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
