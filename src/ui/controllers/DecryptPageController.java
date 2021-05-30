package ui.controllers;

import engine.encryption.Encryptor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ui.utils.UIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DecryptPageController {


    private File file;
    private FileChooser fileChooser = generateFileChooser();
    private FileChooser saveFileChooser = generateSaveFileChooser();

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
    private VBox decryptionVBox;

    @FXML
    private Label decryptionStatusMessage;

    @FXML
    private Button saveDecryptedFileButton;

    @FXML
    private VBox verificationVBox;

    @FXML
    private Label verificationStatusMessage;

    @FXML
    private Button decryptVerifyButton;

    @FXML
    private void chooseFile(ActionEvent event) {
        File file = fileChooser.showOpenDialog(UIUtils.getInstance().getStage());
        if (file != null) {
            this.file = file;
            this.chosenFileLabel.setText(this.file.getAbsolutePath());
            decryptVerifyButton.setDisable(false);
        }
    }

    @FXML
    private void decryptVerify(ActionEvent event) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Encryptor.Decrypt(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

/*
    TODO
    DecryptionVerificationStatus status = DecryptorVerifier.action(this.file);
    u zavisnosti od statusa postaviti odgovarajucu poruku
    Decryption:
        decryptionVBox.setVisible(true);
        decryptionStatusMessage.setText("...")
            -> Decryption succeeded.  (this.saveDecryptedFileButton.setDisabled(false)
            -> Decryption failed (public key KEYID isn't contained in public key ring)  (this.saveDecryptedFileButton.setDisabled(true)
            -> Decryption error (this.saveDecryptedFileButton.setDisabled(true)
    Verification:
        verificationVBox.setVisible(true);
        verificationStatusMessage.setText("...")
            -> Verification succeded
                -> Signer : username <email> KEYID
            -> Verification failed (public key KEYID isn't contained in pkr)
                -> Signer : username <email> KEYID
            -> Verification error
                -> Couldn't find original file
                -> Other error
 */
    }

    @FXML
    private void saveDecryptedFile(ActionEvent event) {
        File file = saveFileChooser.showSaveDialog(UIUtils.getInstance().getStage());
        /**
         * TODO
         * BufferedWriter.write -> [DecryptionVerificationStatus] this.status.getOriginal
         */

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
