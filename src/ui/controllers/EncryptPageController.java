package ui.controllers;


import engine.key_management.entities.KeyInfo;
import engine.key_management.entities.PublicKeyInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bouncycastle.openpgp.examples.DSAElGamalKeyRingGenerator;
import ui.utils.KeyInfoObservableLists;
import ui.utils.UIUtils;

import java.util.stream.Collectors;

public class EncryptPageController {



    @FXML
    private ImageView imageViewBack;

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
    private ComboBox<KeyInfo> publicKeyComboBox;

    @FXML
    private ComboBox<KeyInfo> secretKeyComboBox;


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
        imageViewBack.setImage(new Image(getClass().getResource("..\\resources\\icons\\backBlue.png").toExternalForm()));

        this.publicKeyComboBox.setItems(KeyInfoObservableLists.getPublicKeyObservableList());
        this.secretKeyComboBox.setItems(KeyInfoObservableLists.getSecretKeyObservableList());
    }

    @FXML
    private void backAction(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\home.fxml");
    }
}
