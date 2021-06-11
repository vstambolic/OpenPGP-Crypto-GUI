package ui.controllers;

import engine.key_management.entities.KeyInfo;
import engine.transfer.receiver.Receiver;
import engine.transfer.receiver.ReceiverStatus;
import engine.transfer.receiver.exception.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.bouncycastle.openpgp.PGPException;
import ui.utils.UIUtils;

import java.io.File;
import java.io.IOException;

public class DecryptPageController {

    private File file;
    private final FileChooser fileChooser = generateFileChooser();
    private final FileChooser saveFileChooser = generateSaveFileChooser();

    private static FileChooser generateSaveFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Decrypted File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        return fileChooser;
    }

    private static FileChooser generateFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File to Decrypt/Verify");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SIG", "*.sig"),
                new FileChooser.ExtensionFilter("GPG", "*.gpg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        return fileChooser;
    }

    @FXML
    private ImageView imageViewBack;

    @FXML
    private Label chosenFileLabel;

    @FXML
    private PasswordField passphraseField;

    @FXML
    private Label keyIdLabel;

    @FXML
    private VBox passphraseVBox;

    @FXML
    private VBox decryptionVBox;

    @FXML
    private Label decryptionStatusMessage;

    @FXML
    private Button saveOriginalFileButton;

    @FXML
    private Label saveOriginalFileStatusLabel;

    @FXML
    private VBox verificationVBox;

    @FXML
    private Label verificationStatusMessage;

    @FXML
    private Button decryptVerifyButton;

    private Receiver receiver;

    @FXML
    private void chooseFile(ActionEvent event) {
        File file = fileChooser.showOpenDialog(UIUtils.getInstance().getStage());
        if (file != null) {
            this.file = file;
            this.chosenFileLabel.setText(this.file.getAbsolutePath());
            decryptVerifyButton.setDisable(false);
        }
        this.clear();

    }

    public void clear() {
        this.passphraseField.clear();
        this.passphraseVBox.setVisible(false);
        this.passphraseField.setDisable(false);

        this.decryptionStatusMessage.setText("-");
        this.decryptionVBox.setDisable(true);

        this.verificationStatusMessage.setText("-");
        this.verificationVBox.setDisable(true);

        this.saveOriginalFileButton.setDisable(true);

        this.saveOriginalFileStatusLabel.setText("");

    }

    @FXML
    private void decryptVerify(ActionEvent event) {
        this.decryptVerifyButton.setDisable(true);
        try {
            this.receiver = new Receiver(this.file);
        } catch (IOException e) {
            this.decryptionVBox.setDisable(false);
            this.decryptionStatusMessage.setText("Decryption failed: Invalid file format.");
            return;
        }
        this.receive();
    }

    private void receive() {
        try {
            receiver.receive();
        } catch (InvalidPassphraseException e) {
            this.decryptionVBox.setDisable(false);
            decryptionStatusMessage.setText("Decryption failed: Invalid Passphrase.");
            this.passphraseField.setDisable(false);
            return;
        } catch (PassphraseRequiredException e) {
            decryptionStatusMessage.setText("Decryption failed: Sender's key is protected with passphrase.");
            this.keyIdLabel.setText(e.getKeyIdHexString());
            this.passphraseVBox.setVisible(true);
            return;
        } catch (KeyNotFoundException e) {
            this.decryptionVBox.setDisable(false);
            decryptionStatusMessage.setText("Decryption failed: Unknown sender.\nEncryption key ID: " + e.getKeyIdHexString());
            return;
        } catch (IOException | PGPException | InvalidFileFormatException e) {
            this.decryptionVBox.setDisable(false);
            decryptionStatusMessage.setText("Decryption failed: Unexpected error occurred.");
            return;
        } catch (SignerKeyNotFoundException e) {
            verificationVBox.setDisable(false);
            verificationStatusMessage.setText("Verification failed.\nUnknown signer with key id: "
                    + e.getKeyIdHexString()
                    + "\nSignature created on: " + this.receiver.getReceiverStatus().getSignatureDate());
        }

        ReceiverStatus receiverStatus = receiver.getReceiverStatus();
        if (receiverStatus.isDecryptionApplied()) {
            decryptionVBox.setDisable(false);
            if (receiverStatus.isDecryptionSucceeded()) {
                KeyInfo encryptorKeyInfo = receiverStatus.getEncryptorKeyInfo();
                decryptionStatusMessage.setText("Decryption succeeded!\nEncryptor info: " + encryptorKeyInfo.toStringPretty());
                this.saveOriginalFileButton.setDisable(false);
            } else {
                decryptionStatusMessage.setText("Decryption failed: Unexpected error occurred.");
            }
        }
        if (receiverStatus.isVerificationApplied()) {
            verificationVBox.setDisable(false);
            if (receiverStatus.isVerificationSucceeded()) {
                verificationVBox.setDisable(false);
                verificationStatusMessage.setText("Verification succeeded - Signature is valid!\nSigner info: "
                        + receiverStatus.getSignerKeyInfo().toStringPretty()
                        + "\nSignature created on: " + receiverStatus.getSignatureDate());

                this.saveOriginalFileButton.setDisable(false);
            } else {
                if (receiverStatus.getSignerKeyInfo() != null)
                    verificationStatusMessage.setText("Verification failed - Signature is invalid!\nSigner info: "
                            + receiverStatus.getSignerKeyInfo().toStringPretty()
                            + "\nSignature created on: " + this.receiver.getReceiverStatus().getSignatureDate());
                else
                    verificationStatusMessage.setText("Verification failed - Signature is invalid!\nSignature created on: "
                            + this.receiver.getReceiverStatus().getSignatureDate());
            }
        }
    }

    @FXML
    void passphraseEnteredAction(ActionEvent event) {
        this.passphraseField.setDisable(true);
        this.receiver.setPassphrase(this.passphraseField.getText());
        this.receive();
    }


    @FXML
    private void saveOriginalFile(ActionEvent event) {
        File file = saveFileChooser.showSaveDialog(UIUtils.getInstance().getStage());
        if (file != null)
            try {
                this.receiver.getReceiverStatus().exportOriginalMessage(file);
                this.saveOriginalFileStatusLabel.setText("Successfully saved original file.");
            }
            catch (IOException e) {
                this.saveOriginalFileStatusLabel.setText("Couldn't save original file.");
            }
    }


    @FXML
    private void backAction(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\home.fxml");
    }

    @FXML
    private void initialize() {
        imageViewBack.setImage(new Image(getClass().getResource("..\\resources\\icons\\backRed.png").toExternalForm()));
    }

}
