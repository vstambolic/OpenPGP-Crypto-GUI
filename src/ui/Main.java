package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import ui.utils.UIUtils;

/**
 * Main
 * @author VStambolic
 */

/**
 * TODO
 * 2) show keys ???
 * 3) Decrypt Page
 * 4) Encrypt Page controls
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources\\home.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("resources\\decryptPage.fxml"));
        primaryStage.setTitle("OpenPGP Crypto GUI");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("resources\\style.css").toExternalForm());
        UIUtils.setStage(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
