package ui.controllers;

import engine.transfer.sender.Sender;
import engine.key_management.KeyManager;
import engine.key_management.entities.KeyInfo;
import engine.key_management.entities.PublicKeyInfo;
import engine.key_management.entities.SecretKeyInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import ui.utils.KeyInfoObservableLists;
import ui.utils.UIUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class EncryptPageController {


    @FXML
    private Pane encryptOptionsPane;
    @FXML
    private Pane signOptionsPane;

    @FXML
    private Pane passphraseOptionPane;

    @FXML
    private ComboBox<KeyInfo> publicKeyComboBox;

    @FXML
    private ComboBox<KeyInfo> secretKeyComboBox;

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
    private PasswordField passphraseField;

    @FXML
    private Button exportButton;


    private File file;
    private File outputDirectory;

    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private void chooseFileButtonAction(ActionEvent event) {
        fileChooser.setTitle("Choose File to Encrypt/Sign");
        File file = fileChooser.showOpenDialog(UIUtils.getInstance().getStage());
        if (file != null) {
            this.file = file;
            this.chooseFileButton.setText(file.getAbsolutePath());
            toggleDisableExport();
        }
    }

    @FXML
    private void chooseOutputDirectoryButtonAction(ActionEvent event) {
        directoryChooser.setTitle("Choose Output Directory");
        File directory = directoryChooser.showDialog(UIUtils.getInstance().getStage());

        if (directory != null) {
            this.outputDirectory = directory;
            this.chooseOutputDirectoryButton.setText(outputDirectory.getAbsolutePath());
            toggleDisableExport();
        }

    }

    @FXML
    private void encryptCheckboxAction(ActionEvent event) {
        this.encryptOptionsPane.setDisable(!this.encryptCheckbox.isSelected());
        this.refreshExportButton();
    }

    @FXML
    private void signCheckboxAction(ActionEvent event) {
        this.signOptionsPane.setDisable(!this.signCheckbox.isSelected());
        if (!this.signCheckbox.isSelected())
            this.passphraseField.setText("");
        this.refreshExportButton();
    }

    private void refreshExportButton() {
        if (this.encryptCheckbox.isSelected() && this.signCheckbox.isSelected()) {
            this.exportButton.setText("Encrypt/Sign");
            this.statusLabel.setText("");
        } else if (this.encryptCheckbox.isSelected()) {
            this.exportButton.setText("Encrypt");
            this.statusLabel.setText("");
        } else if (this.signCheckbox.isSelected()) {
            this.exportButton.setText("Sign");
            this.statusLabel.setText("");
        } else {
            this.exportButton.setText("Encrypt/Sign");
            this.statusLabel.setText("Choose Encrypt/Sign option.");
        }
        this.toggleDisableExport();
    }

    private void toggleDisableExport() {
        this.exportButton.setDisable(this.file == null || this.outputDirectory == null || !(this.encryptCheckbox.isSelected() || this.signCheckbox.isSelected()));
    }

    @FXML
    void secretKeySelectedAction(ActionEvent event) {
        try {
            this.passphraseOptionPane.setDisable(!KeyManager.isEncrypted(this.secretKeyComboBox.getSelectionModel().getSelectedItem()));
        } catch (PGPException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportAction(ActionEvent event) {
        Sender sender = new Sender(this.file,this.outputDirectory, this.compressCheckbox.isSelected(),this.radix64Checkbox.isSelected(),this.encryptCheckbox.isSelected(),(int)this.symmetricAlgorithm.getSelectedToggle().getUserData(),(PublicKeyInfo) this.publicKeyComboBox.getValue(),this.signCheckbox.isSelected(),this.passphraseField.getText(),(SecretKeyInfo) this.secretKeyComboBox.getValue());
        try {
            sender.send();
        } catch (RuntimeException | FileNotFoundException e) {
            this.statusLabel.setText(e.getMessage());
            return;
        }

        if (this.signCheckbox.isSelected() && this.encryptCheckbox.isSelected())
            this.statusLabel.setText("Encryption/Signing succeeded.");
        else
            if (this.encryptCheckbox.isSelected())
                this.statusLabel.setText("Encryption succeeded.");
            else
                this.statusLabel.setText("Signing succeeded.");
    }

    @FXML
    private void initialize() {

        imageViewBack.setImage(new Image(getClass().getResource("..\\resources\\icons\\backBlue.png").toExternalForm()));

        this.symmetricAlgorithm.getToggles().get(0).setUserData(PGPEncryptedData.TRIPLE_DES);
        this.symmetricAlgorithm.getToggles().get(1).setUserData(PGPEncryptedData.CAST5);
        this.publicKeyComboBox.setItems(KeyInfoObservableLists.getPublicKeyObservableList());
        this.secretKeyComboBox.setItems(KeyInfoObservableLists.getSecretKeyObservableList());
    }

    @FXML
    private void backAction(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\home.fxml");
    }
}
