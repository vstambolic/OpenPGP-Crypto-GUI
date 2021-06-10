package engine.transfer.receiver;

import engine.key_management.entities.KeyInfo;

import java.util.Date;

public class RecieverStatus {

    private boolean decryptionApplied;
    private boolean decryptionSucceeded;
    private boolean verificationApplied;
    private boolean verificationSucceeded;

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

    private Date verificationDate;
    private KeyInfo signerKeyInfo;
    private KeyInfo encryptorKeyInfo;

    public KeyInfo getSignerKeyInfo() {
        return signerKeyInfo;
    }

    public void setSignerKeyInfo(KeyInfo signerKeyInfo) {
        this.signerKeyInfo = signerKeyInfo;
    }

    public StringBuilder stringBuilder = new StringBuilder();

    public void appendMessage(char chr) {
        stringBuilder.append(chr);
    }

    public void setVerificationDate(Date date) {
        this.verificationDate = date;
    }
}
