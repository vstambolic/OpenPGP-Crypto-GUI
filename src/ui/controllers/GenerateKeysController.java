package ui.controllers;

import engine.key_management.key_material.SubkeyMaterial;
import engine.key_management.key_material.KeyMaterial;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.security.Key;

public class GenerateKeysController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passphraseTextField;

    @FXML
    private ListView<KeyMaterial> singatureListView;

    @FXML
    private ListView<SubkeyMaterial> encryptionListView;

    @FXML
    private void initialize() {
        singatureListView.getItems().addAll(KeyMaterial.values());
        singatureListView.getSelectionModel().select(KeyMaterial.DSA_1024);
        encryptionListView.getItems().addAll(SubkeyMaterial.values());
        encryptionListView.getSelectionModel().select(SubkeyMaterial.EL_GAMAL_1024);

    }

    public String getEmail() {
        return emailTextField.getText();
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassphrase() {
        return passphraseTextField.getText();
    }

    public KeyMaterial getKeyMaterial() {
        return this.singatureListView.getSelectionModel().getSelectedItem();
    }
    public SubkeyMaterial getSubkeyMaterial() {
        return this.encryptionListView.getSelectionModel().getSelectedItem();
    }

}
