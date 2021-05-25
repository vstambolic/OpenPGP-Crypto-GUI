package ui.controllers;

import engine.encryption.Encryptor;
import engine.key_management.KeyManager;
import engine.key_management.entities.KeyInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.PGPException;
import ui.utils.KeyInfoObservableLists;
import ui.utils.UIUtils;

import java.io.File;
import java.nio.file.Files;

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
        this.file = fileChooser.showOpenDialog(UIUtils.getInstance().getStage());
        if (this.file != null)
            this.chooseFileButton.setText(file.getAbsolutePath());
    }

    @FXML
    private void chooseOutputDirectoryButtonAction(ActionEvent event) {
        directoryChooser.setTitle("Choose Output Directory");
        this.outputDirectory = directoryChooser.showDialog(UIUtils.getInstance().getStage());
        if (this.outputDirectory != null)
            this.chooseOutputDirectoryButton.setText(outputDirectory.getAbsolutePath());

    }

    @FXML
    private void encryptCheckboxAction(ActionEvent event) {
        this.encryptOptionsPane.setDisable(!this.encryptCheckbox.isSelected());
        this.refreshExportButton();
    }

    @FXML
    private void signCheckboxAction(ActionEvent event) {
        this.signOptionsPane.setDisable(!this.signCheckbox.isSelected());
        this.refreshExportButton();
    }
    private void refreshExportButton() {
        if (this.encryptCheckbox.isSelected() && this.signCheckbox.isSelected()) {
            this.exportButton.setText("Encrypt/Sign");
            this.exportButton.setDisable(false);
            this.statusLabel.setText("");

        }
        else
            if (this.encryptCheckbox.isSelected()) {
                this.exportButton.setText("Encrypt");
                this.exportButton.setDisable(false);
                this.statusLabel.setText("");
            }
            else
                if (this.signCheckbox.isSelected()) {
                    this.exportButton.setText("Sign");
                    this.exportButton.setDisable(false);
                    this.statusLabel.setText("");
                }
                else {
                    this.exportButton.setText("Encrypt/Sign");
                    this.exportButton.setDisable(true);
                    this.statusLabel.setText("Choose Encrypt/Sign option.");
                }

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
        try{
            Encryptor.Encrypt(Files.readAllBytes(file.toPath()), ((RadioButton)this.symmetricAlgorithm.getSelectedToggle()).getText(), null,
                    null, this.passphraseField.getText(), file.getName(), outputDirectory.getPath(),
                    this.encryptCheckbox.isSelected(), this.compressCheckbox.isSelected(), this.radix64Checkbox.isSelected(), this.signCheckbox.isSelected());
        }catch (Exception e){
            e.printStackTrace();
        }

        /**
         * TODO
         * U zavisnosti od izabrane opcije
         *      -> Encryptor.encrypt (.gpg file)
         *      -> Signer.sign (.sig file)
         *      -> EncryptorSigner.encryptSign (.gpg file)
         * statusLabel.setText("")
         *      -> Encryption succeeded.
         *      -> Signing succeeded
         *      -> Encryption failed
         *      -> Invalid passphrase for secret key
         *      -> Public/Secret key not chosen
         *
         */
    }


    @FXML
    private void initialize() {
        imageViewBack.setImage(new Image(getClass().getResource("..\\resources\\icons\\backBlue.png").toExternalForm()));

        this.publicKeyComboBox.setItems(KeyInfoObservableLists.getPublicKeyObservableList());
        this.secretKeyComboBox.setItems(KeyInfoObservableLists.getSecretKeyObservableList());
    }

    @FXML
    private void backAction(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\home.fxml");
    }
}
