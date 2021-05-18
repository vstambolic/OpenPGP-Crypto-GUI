package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ui.utils.UIUtils;

public class HomeController {

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
    private void showKeyManagementPage(ActionEvent event) {        UIUtils.getInstance().switchPages("..\\resources\\keyManagementPage.fxml");

    }

}
