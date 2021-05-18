package ui.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

public class EncryptPageController {



    @FXML
    private CheckBox encryptCheckbox;

    @FXML
    private ToggleGroup symmetricAlgorithm;

    @FXML
    private CheckBox signCheckbox;

    @FXML
    private CheckBox compressCheckbox;

    @FXML
    private CheckBox radix64Checkbox;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button chooseOutputDirectoryButton;

    @FXML
    private Label statusLabel;

    @FXML
    void chooseFileButtonAction(ActionEvent event) {

    }

    @FXML
    void chooseOutputDirectoryButtonAction(ActionEvent event) {

    }

    @FXML
    void exportMessageAction(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert encryptCheckbox != null : "fx:id=\"encryptCheckbox\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert symmetricAlgorithm != null : "fx:id=\"symetricAlgorithm\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert signCheckbox != null : "fx:id=\"signCheckbox\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert compressCheckbox != null : "fx:id=\"compressCheckbox\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert radix64Checkbox != null : "fx:id=\"radix64Checkbox\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert chooseFileButton != null : "fx:id=\"chooseFileButton\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert chooseOutputDirectoryButton != null : "fx:id=\"chooseOutputDirectoryButton\" was not injected: check your FXML file 'encryptPage.fxml'.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected: check your FXML file 'encryptPage.fxml'.";

    }
}
