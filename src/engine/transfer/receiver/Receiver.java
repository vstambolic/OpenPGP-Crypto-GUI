package engine.transfer.receiver;

import engine.key_management.KeyManager;
import engine.key_management.entities.PublicKeyInfo;
import engine.key_management.entities.SecretKeyInfo;
import engine.transfer.receiver.exception.*;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Receiver {

    private static BcKeyFingerprintCalculator KEY_FINGERPRINT_CALCULATOR = new BcKeyFingerprintCalculator();

    private PGPObjectFactory objectFactory;
    private Object currObject;
    private String passphrase = "";
    private PGPSecretKey secretMasterKey;
    private PGPSecretKey secretSubKey;
    private PGPPublicKeyEncryptedData encryptedData;
    private ReceiverStatus status = new ReceiverStatus();
    private File file;


    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public Receiver(File file) throws IOException {
        this.file = file;
        FileInputStream fis = new FileInputStream(file);
        this.objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(fis), KEY_FINGERPRINT_CALCULATOR);
        this.currObject = this.objectFactory.nextObject();
    }

    public void receive() throws KeyNotFoundException, PassphraseRequiredException, InvalidPassphraseException, IOException, PGPException, InvalidFileFormatException, SignerKeyNotFoundException {

        if (currObject instanceof PGPEncryptedDataList) {
            if (!this.status.isDecryptionApplied()) {
                this.status.setDecryptionApplied(true);

                PGPEncryptedDataList dataList = (PGPEncryptedDataList) currObject;
                this.encryptedData = (PGPPublicKeyEncryptedData) dataList.get(0);

                PGPSecretKeyRing skrskr = null;
                try {
                    skrskr = KeyManager.getSecretKeyRing(encryptedData.getKeyID());
                    final Iterator<PGPSecretKey> secretKeys = skrskr.getSecretKeys();
                    this.secretMasterKey = secretKeys.next();
                    this.secretSubKey = secretKeys.next();
                } catch (NullPointerException | PGPException e) {
                    throw new KeyNotFoundException(encryptedData.getKeyID());
                }
                this.status.setEncryptorKeyInfo(new SecretKeyInfo(this.secretMasterKey));

                if (KeyManager.isEncrypted(this.secretSubKey))
                    throw new PassphraseRequiredException(this.secretMasterKey.getKeyID());
            }
            this.decrypt();
        }
        if (this.currObject instanceof PGPCompressedData)
            this.decompress();
        if (this.currObject instanceof PGPOnePassSignatureList)
            this.verify();
        else if (this.currObject instanceof PGPLiteralData)
            this.read();
        else
            throw new InvalidFileFormatException();
    }

    public void decrypt() throws PassphraseRequiredException, InvalidPassphraseException, IOException, PGPException, InvalidFileFormatException, SignerKeyNotFoundException {
        if (KeyManager.isEncrypted(this.secretSubKey) && this.passphrase == null)
            throw new PassphraseRequiredException(this.secretMasterKey.getKeyID());

        PGPPrivateKey privateKey = null;
        try {
            privateKey = KeyManager.extractPrivateKey(this.secretSubKey, this.passphrase);
        } catch (PGPException e) {
            throw new InvalidPassphraseException(this.secretMasterKey.getKeyID());
        }

        // Decryption
        InputStream plainStream = this.encryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));
        this.status.setDecryptionSucceeded(true);
        this.objectFactory = new PGPObjectFactory(plainStream, KEY_FINGERPRINT_CALCULATOR);
        this.currObject = objectFactory.nextObject();

    }

    private void decompress() throws IOException, PGPException {
        PGPCompressedData compressedData = (PGPCompressedData) this.currObject;
        this.objectFactory = new PGPObjectFactory(compressedData.getDataStream(), KEY_FINGERPRINT_CALCULATOR);
        this.currObject = this.objectFactory.nextObject();
    }

    private void read() throws IOException {
        InputStream literalStream = ((PGPLiteralData) currObject).getInputStream();
        int ch;
        while ((ch = literalStream.read()) >= 0)
            this.status.appendChar((char) ch);
    }

    private void verify() throws SignerKeyNotFoundException {
        this.status.setVerificationApplied(true);
        try {
            this.status.setSignatureDate(new Date(
                            Files.readAttributes(this.file.toPath(), BasicFileAttributes.class)
                                    .creationTime()
                                    .to(TimeUnit.MILLISECONDS)
                    )
            );
        } catch (IOException e) {
            return;
        }
        this.status.setDecryptionSucceeded(true);
        PGPOnePassSignatureList signatureList = (PGPOnePassSignatureList) this.currObject;
        PGPOnePassSignature signature = signatureList.get(0);
        PGPPublicKey signerPublicKey = null;
        try {
            signerPublicKey = KeyManager.getPublicKey(signature.getKeyID());
            this.status.setSignerKeyInfo(new PublicKeyInfo(signerPublicKey));
        } catch (NullPointerException | PGPException e) {
            throw new SignerKeyNotFoundException(signature.getKeyID());
        }

        try {
            signature.init(new BcPGPContentVerifierBuilderProvider(), signerPublicKey);

            PGPLiteralData literalData = null;
            literalData = (PGPLiteralData) objectFactory.nextObject();
            InputStream literalStream = literalData.getInputStream();
            int ch;
            while ((ch = literalStream.read()) >= 0) {
                signature.update((byte) ch);
                this.status.appendChar((char) ch);
            }
            PGPSignature verificationSignature = ((PGPSignatureList) objectFactory.nextObject()).get(0);
            boolean verificationStatus = signature.verify(verificationSignature);
            this.status.setVerificationSucceeded(verificationStatus);

        } catch (PGPException | IOException e) {
            this.status.setVerificationSucceeded(false);
        }

    }

    public ReceiverStatus getReceiverStatus() {
        return status;
    }


}
