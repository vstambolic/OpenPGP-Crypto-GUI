package ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UIUtils {
    private static UIUtils instance;
    private Stage stage;

    public static void setStage(Stage stage) {
        getInstance().stage = stage;
    }

    private UIUtils() {
    }

    public static UIUtils getInstance() {
        if (instance == null)
            instance = new UIUtils();
        return instance;
    }

    public void switchPages(String fxmlPath) {

        Parent parent = null;
        try {
            parent = FXMLLoader.load(instance.getClass().getResource(fxmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.stage.getScene().setRoot(parent);
    }

    public Stage getStage() {
        return this.stage;
    }
}
