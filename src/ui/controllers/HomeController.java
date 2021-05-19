package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ui.utils.UIUtils;

public class HomeController {


    @FXML
    private ImageView imageViewLocked;

    @FXML
    private ImageView imageViewUnlocked;

    @FXML
    private ImageView imageViewKeys;

    @FXML
    private void initialize() {
        imageViewLocked.setImage(new Image(getClass().getResource("..\\resources\\icons\\locked.png").toExternalForm()));
        imageViewKeys.setImage(new Image(getClass().getResource("..\\resources\\icons\\keys.png").toExternalForm()));
        imageViewUnlocked.setImage(new Image(getClass().getResource("..\\resources\\icons\\unlocked.png").toExternalForm()));
    }

    @FXML
    private StackPane root;

    @FXML
    private GridPane home;

    @FXML
    private AnchorPane welcome;

    @FXML
    private void hide(MouseEvent event) {
        this.welcome.setVisible(false);
        this.home.setVisible(true);
        System.out.println("siso");
    }

    @FXML
    private void showDecryptPage(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\decryptPage.fxml");
    }

    @FXML
    private void showEncryptPage(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\encryptPage.fxml");

    }

    @FXML
    private void showKeyManagementPage(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\keyManagementPage.fxml");

    }

}
