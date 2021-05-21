package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class PassphraseDialogController {

    @FXML
    private Label label;
    @FXML
    private PasswordField passphraseField;

    public void setLabelText(String text) {
        this.label.setText(text);
    }
    public String getPassphrase() {
        return this.passphraseField.getText();
    }
}
