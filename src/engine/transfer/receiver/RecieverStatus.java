package engine.transfer.receiver;

import engine.key_management.entities.KeyInfo;

import java.util.Date;

public class RecieverStatus {

    private boolean decryptionApplied;
    private boolean decryptionSucceeded;
    private boolean verificationApplied;
    private boolean verificationSucceeded;

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

    public StringBuilder stringBuilder = new StringBuilder();

    public void appendMessage(char chr) {
        stringBuilder.append(chr);
    }

    public void setVerificationDate(Date date) {
        this.verificationDate = date;
    }
}
