package ui.controllers;


import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.List;
import java.util.Optional;

//import engine.key_management.KeyManager;
import engine.key_management.KeyManager;
import engine.key_management.entities.KeyInfo;
import engine.key_management.entities.PublicKeyInfo;
import engine.key_management.entities.SecretKeyInfo;
import engine.key_management.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.bouncycastle.openpgp.PGPException;
import ui.utils.KeyInfoObservableLists;
import ui.utils.UIUtils;


public class KeyManagementController {

    @FXML
    private TableView<KeyInfo> publicKeyTable;
    @FXML
    private TableColumn<KeyInfo, String> publicKeyTableUserCol;
    @FXML
    private TableColumn<KeyInfo, String> publicKeyTableEmailCol;
    @FXML
    private TableColumn<KeyInfo, String> publicKeyTableFingerprintCol;


    @FXML
    private TableView<KeyInfo> secretKeyTable;
    @FXML
    private TableColumn<KeyInfo, String> secretKeyTableUserCol;
    @FXML
    private TableColumn<KeyInfo, String> secretKeyTableEmailCol;
    @FXML
    private TableColumn<KeyInfo, String> secretKeyTableFingerprintCol;
    @FXML
    private ImageView imageViewBack;

    @FXML
    private Label statusLabel;

    private final FileChooser fileChooser = generateFileChooser();

    private static FileChooser generateFileChooser() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ASC", "*.asc"),
                new FileChooser.ExtensionFilter("GPG", "*.gpg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        return fileChooser;
    }



    @FXML
    private void deletePublicKey(ActionEvent event) {
        if (this.publicKeyTable.getItems().isEmpty())
            return;

        KeyInfo keyInfo = this.publicKeyTable.getSelectionModel().getSelectedItem();
        if (keyInfo != null) {
            statusLabel.setText("Deleting key...");
            try {
                KeyManager.deletePublicKey(keyInfo);
                KeyInfoObservableLists.getPublicKeyObservableList().remove(keyInfo);
                statusLabel.setText("Deleted public key: " + keyInfo.toString());
            } catch (PGPException e) {
                statusLabel.setText("Error occurred while deleting key.");
            }
        } else
            statusLabel.setText("Choose public key you wish to delete.");
    }

    @FXML
    private void deleteSecretKey(ActionEvent event) {
        if (this.secretKeyTable.getItems().isEmpty())
            return;


        KeyInfo keyInfo = this.secretKeyTable.getSelectionModel().getSelectedItem();
        if (keyInfo != null) {
            try {
                if (KeyManager.isEncrypted(keyInfo)){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("..\\resources\\passphraseDialog.fxml"));
                    DialogPane dialogPane = null;
                    try {
                        dialogPane = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialogPane.getStylesheets().add(getClass().getResource("..\\resources\\style.css").toExternalForm());

                    PassphraseDialogController controller = loader.getController();
                    alert.setTitle("Enter Passphrase");
                    alert.setDialogPane(dialogPane);
                    ButtonType buttonType = alert.showAndWait().get();
                    if (buttonType == ButtonType.OK)
                        try {
                            KeyManager.deleteSecretKey(keyInfo, controller.getPassphrase());
                            KeyInfoObservableLists.getSecretKeyObservableList().remove(keyInfo);
                            statusLabel.setText("Deleted secret key: " + keyInfo);
                        }
                        catch (PGPException e) {
                            statusLabel.setText("Incorrect passphrase.");
                        }
                }
                else {
                    KeyManager.deleteSecretKey(keyInfo, "");
                    KeyInfoObservableLists.getSecretKeyObservableList().remove(keyInfo);
                    statusLabel.setText("Deleted secret key: " + keyInfo);
                }
            } catch (PGPException e) {
                statusLabel.setText("Error occurred while deleting key.");
            }
        }
        else {
            statusLabel.setText("Choose secret key you wish to delete.");
        }
    }



    @FXML
    private void exportPublicKey(ActionEvent event) {

        if (this.publicKeyTable.getItems().isEmpty())
            return;

        KeyInfo keyInfo = this.publicKeyTable.getSelectionModel().getSelectedItem();
        if (keyInfo != null) {
            fileChooser.setTitle("Export Public Key");
            fileChooser.setInitialFileName(keyInfo.toString() + "_public");
            File file = fileChooser.showSaveDialog(UIUtils.getInstance().getStage());

            if (file != null) {
                statusLabel.setText("Exporting key...");
                try {
                    KeyManager.exportKey(keyInfo, file);
                    statusLabel.setText("Exported public key: " + keyInfo);
                } catch (IOException | PGPException e) {
                    statusLabel.setText("Error occurred while exporting key.");
                }
            }
        } else
            statusLabel.setText("Choose public key you wish to export.");
    }

    @FXML
    private void exportSecretKey(ActionEvent event) {

        if (this.publicKeyTable.getItems().isEmpty())
            return;

        KeyInfo keyInfo = this.secretKeyTable.getSelectionModel().getSelectedItem();
        if (keyInfo != null) {
            fileChooser.setTitle("Export Secret Key");
            fileChooser.setInitialFileName(keyInfo.toString() + "_secret");
            File file = fileChooser.showSaveDialog(UIUtils.getInstance().getStage());
            if (file != null) {
                statusLabel.setText("Exporting key...");
                try {
                    KeyManager.exportKey(keyInfo, file);
                    statusLabel.setText("Exported secret key: " + keyInfo);
                } catch (IOException | PGPException e) {
                    statusLabel.setText("Error occurred while exporting key.");
                }
            }
        } else
            statusLabel.setText("Choose public key you wish to export.");
    }
    FXMLLoader loader = new FXMLLoader(getClass().getResource("..\\resources\\passphraseDialog.fxml"));
    PassphraseDialogController controller = loader.getController();

    @FXML
    private void generateNewKeyPair(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("..\\resources\\generateKeysDialog.fxml"));
        DialogPane dialogPane = null;
        try {
            dialogPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialogPane.getStylesheets().add(getClass().getResource("..\\resources\\style.css").toExternalForm());

        GenerateKeysController controller = loader.getController();
        alert.setTitle("Generate Key Pair");
        alert.setDialogPane(dialogPane);
        ButtonType buttonType =  alert.showAndWait().get();
        if (buttonType == ButtonType.OK) {
            try {
                statusLabel.setText("Generating new key pair...");
                Pair<PublicKeyInfo, SecretKeyInfo> keyPairInfo = KeyManager.generateKeys(new User(controller.getUsername(), controller.getEmail(), controller.getPassphrase()), controller.getKeyMaterial(), controller.getSubkeyMaterial());
                KeyInfoObservableLists.getPublicKeyObservableList().add(keyPairInfo.getKey());
                KeyInfoObservableLists.getSecretKeyObservableList().add(keyPairInfo.getValue());
                statusLabel.setText("New key pair generated successfully.");
            } catch (Exception e) {
                statusLabel.setText("Error occurred while generating key pair.");
            }

        }

    }



    @FXML
    private void importKeys(ActionEvent event) {
        fileChooser.setTitle("Import Keys");
        File file = fileChooser.showOpenDialog(UIUtils.getInstance().getStage());
        if (file != null) {

            try {
                List<KeyInfo> keyInfoList = KeyManager.importKeyRings(file);

                String statusMessage = "Imported " + keyInfoList.size();

                if (keyInfoList.isEmpty())
                    statusMessage += " new keys.";
                else if (keyInfoList.get(0) instanceof PublicKeyInfo) {
                    statusMessage += " new public key(s).";
                    KeyInfoObservableLists.getPublicKeyObservableList().addAll(keyInfoList);
                } else {
                    statusMessage += " new secret key(s).";
                    KeyInfoObservableLists.getSecretKeyObservableList().addAll(keyInfoList);
                }
                statusLabel.setText(statusMessage);

            } catch (Exception e) {
                statusLabel.setText("Error occurred while importing key(s).");
            }
        }

    }

    @FXML
    private void showPublicKey(ActionEvent event) {

    }

    @FXML
    private void showSecretKey(ActionEvent event) {

    }

    @FXML
    private void backAction(ActionEvent event) {
        UIUtils.getInstance().switchPages("..\\resources\\home.fxml");
    }

    @FXML
    private void initialize() {
        imageViewBack.setImage(new Image(getClass().getResource("..\\resources\\icons\\backTeal.png").toExternalForm()));
        initializeTables();
        statusLabel.setText("");
    }

    private void initializeTables() {

        // Initialize public key table
        this.publicKeyTableUserCol.setCellValueFactory(new PropertyValueFactory("username"));
        this.publicKeyTableEmailCol.setCellValueFactory(new PropertyValueFactory("email"));
        this.publicKeyTableFingerprintCol.setCellValueFactory(new PropertyValueFactory("keyId"));

        this.publicKeyTable.setItems(KeyInfoObservableLists.getPublicKeyObservableList());

        // Initialize secret key table
        this.secretKeyTableUserCol.setCellValueFactory(new PropertyValueFactory("username"));
        this.secretKeyTableEmailCol.setCellValueFactory(new PropertyValueFactory("email"));
        this.secretKeyTableFingerprintCol.setCellValueFactory(new PropertyValueFactory("keyId"));

        this.secretKeyTable.setItems(KeyInfoObservableLists.getSecretKeyObservableList());


    }
}
