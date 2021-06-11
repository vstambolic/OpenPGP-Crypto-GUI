package engine.transfer.receiver;

import engine.key_management.entities.KeyInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ReceiverStatus {

    private boolean decryptionApplied;
    private boolean decryptionSucceeded;
    private boolean verificationApplied;
    private boolean verificationSucceeded;
    private Date signatureDate;


    private KeyInfo signerKeyInfo;
    private KeyInfo encryptorKeyInfo;

    public KeyInfo getEncryptorKeyInfo() {
        return encryptorKeyInfo;
    }

    public void setEncryptorKeyInfo(KeyInfo encryptorKeyInfo) {
        this.encryptorKeyInfo = encryptorKeyInfo;
    }

    public boolean isDecryptionApplied() {
        return decryptionApplied;
    }

    public boolean isVerificationApplied() {
        return verificationApplied;
    }

    public boolean isDecryptionSucceeded() {
        return decryptionSucceeded;
    }

    public boolean isVerificationSucceeded() {
        return verificationSucceeded;
    }

    public void setDecryptionApplied(boolean decryptionApplied) {
        this.decryptionApplied = decryptionApplied;
    }

    public void setDecryptionSucceeded(boolean decryptionSucceeded) {
        this.decryptionSucceeded = decryptionSucceeded;
    }

    public void setVerificationApplied(boolean verificationApplied) {
        this.verificationApplied = verificationApplied;
    }

    public void setVerificationSucceeded(boolean verificationSucceeded) {
        this.verificationSucceeded = verificationSucceeded;
    }

    public KeyInfo getSignerKeyInfo() {
        return signerKeyInfo;
    }

    public void setSignerKeyInfo(KeyInfo signerKeyInfo) {
        this.signerKeyInfo = signerKeyInfo;
    }


    public void setSignatureDate(Date date) {
        this.signatureDate = date;
    }
    public Date getSignatureDate() {
        return this.signatureDate;
    }


    private StringBuilder stringBuilder = new StringBuilder();

    public void appendChar(char chr) {
        stringBuilder.append(chr);
    }

    public void exportOriginalMessage(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(this.stringBuilder.toString());
        writer.close();
    }
}
