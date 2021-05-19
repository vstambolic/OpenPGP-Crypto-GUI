package ui.controllers;



import java.io.IOException;
import java.lang.String;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ui.utils.UIUtils;

public class KeyManagementController {

    @FXML
    private TableView<Shit> publicKeyTable;
    @FXML
    private TableColumn<Shit, String> publicKeyTableUserCol;
    @FXML
    private TableColumn<Shit, String> publicKeyTableEmailCol;
    @FXML
    private TableColumn<Shit, String> publicKeyTableFingerprintCol;


    @FXML
    private TableView<Shit> secretKeyTable;
    @FXML
    private TableColumn<Shit, String>  secretKeyTableUserCol;
    @FXML
    private TableColumn<Shit, String> secretKeyTableEmailCol;
    @FXML
    private TableColumn<Shit, String> secretKeyTableFingerprintCol;


    @FXML
    private ImageView imageViewBack;

    @FXML
    private void deletePublicKey(ActionEvent event) {

    }

    @FXML
    private void deleteSecretKey(ActionEvent event) {

    }

    @FXML
    private void exportPublicKey(ActionEvent event) {

    }

    @FXML
    private void exportSecretKey(ActionEvent event) {

    }

    @FXML
    private void generateNewKeyPair(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("..\\resources\\generateKeysDialog.fxml"));
        DialogPane dp = null;
        try {
            dp = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dp.getStylesheets().add(getClass().getResource("..\\resources\\style.css").toExternalForm());

        GenerateKeysController controller = loader.getController();

        alert.setTitle("Generate Key Pair");
        alert.setDialogPane(dp);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            controller.getUsername();
            controller.getEmail();
            controller.getPassphrase();

            // TODO Generate new key pair
        }

    }

    @FXML
    private void importPublicKey(ActionEvent event) {

    }

    @FXML
    private void importSecretKey(ActionEvent event) {

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
        Image mo = new Image(getClass().getResource("..\\resources\\icons\\back.png").toExternalForm());
//        imageViewBack.setImage(new Image(getClass().getResource("..\\resources\\icons\\back.png").toExternalForm()));
        initTables();
//
//        Shit shit = new Shit();
//        ObservableList<Shit> observableList = FXCollections.observableArrayList();
//        observableList.add(shit);
//        this.publicKeyTable.setItems(observableList);

        assert publicKeyTable != null : "fx:id=\"publicKeyTable\" was not injected: check your FXML file 'keyManagementPage.fxml'.";
        assert secretKeyTable != null : "fx:id=\"secretKeyTable\" was not injected: check your FXML file 'keyManagementPage.fxml'.";
    }

    private void initTables() {

        this.publicKeyTableUserCol.setCellValueFactory(new PropertyValueFactory("user"));
        this.publicKeyTableEmailCol.setCellValueFactory(new PropertyValueFactory("email"));
        this.publicKeyTableFingerprintCol.setCellValueFactory(new PropertyValueFactory("fingerprint"));

        this.secretKeyTableUserCol.setCellValueFactory(new PropertyValueFactory("user"));
        this.secretKeyTableEmailCol.setCellValueFactory(new PropertyValueFactory("email"));
        this.secretKeyTableFingerprintCol.setCellValueFactory(new PropertyValueFactory("fingerprint"));

    }
}
