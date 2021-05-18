package ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UIUtils {
    private static UIUtils instance;
    private static Scene scene;

    public static void setScene(Scene _scene) {
        scene = _scene;
    }

    private UIUtils() {
    }

    public static UIUtils getInstance() {
        if (instance==null)
            instance = new UIUtils();
        return instance;
    }

    public static Scene getScene() {
        return scene;
    }

    public void switchPages(String fxmlPath) {

        Parent parent = null;
        try {
            parent = FXMLLoader.load(instance.getClass().getResource(fxmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene.setRoot(parent);
    }
}
