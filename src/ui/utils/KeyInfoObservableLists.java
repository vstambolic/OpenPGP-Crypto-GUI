package ui.utils;

import engine.key_management.KeyManager;
import engine.key_management.entities.KeyInfo;
import engine.key_management.entities.PublicKeyInfo;
import engine.key_management.entities.KeyInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.Key;

public class KeyInfoObservableLists {
    private static final ObservableList<KeyInfo> publicKeyObservableList = FXCollections.observableArrayList();
    private static final ObservableList<KeyInfo> secretKeyObservableList = FXCollections.observableArrayList();

    static {
        publicKeyObservableList.addAll(KeyManager.getPublicKeyInfoCollection());
        secretKeyObservableList.addAll(KeyManager.getSecretKeyInfoCollection());

    }

    public static ObservableList<KeyInfo> getPublicKeyObservableList() {
        return publicKeyObservableList;
    }

    public static ObservableList<KeyInfo> getSecretKeyObservableList() {
        return secretKeyObservableList;
    }
}
