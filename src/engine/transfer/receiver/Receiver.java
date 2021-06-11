package engine.transfer.receiver;

import engine.key_management.KeyManager;
import engine.transfer.receiver.exception.InvalidFileFormatException;
import engine.transfer.receiver.exception.InvalidPassprhaseException;
import engine.transfer.receiver.exception.KeyNotFoundException;
import engine.transfer.receiver.exception.PassphraseRequiredException;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

import java.io.*;
import java.util.Iterator;

public class Receiver {
    private static final int BUFFER_SIZE = 1024;
    // Utils
    private static BcKeyFingerprintCalculator KEY_FINGERPRINT_CALCULATOR = new BcKeyFingerprintCalculator();


    private PGPObjectFactory objectFactory;
    private Object currObject;
    private String passphrase = "";
    private PGPSecretKey secretMasterKey;
    private PGPSecretKey secretSubkey;
    private PGPPublicKeyEncryptedData encryptedData;
    private RecieverStatus status = new RecieverStatus();
    private File file;

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public Receiver(File file) throws IOException {
        this.objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(new FileInputStream(this.file = file)), KEY_FINGERPRINT_CALCULATOR);
        this.currObject = this.objectFactory.nextObject();
    }

    public void receive() throws KeyNotFoundException, PassphraseRequiredException, InvalidPassprhaseException, IOException, PGPException, InvalidFileFormatException {

        if (currObject instanceof PGPEncryptedDataList) {

            PGPEncryptedDataList dataList = (PGPEncryptedDataList) currObject;
            this.encryptedData = (PGPPublicKeyEncryptedData) dataList.get(0);

            PGPSecretKeyRing skrskr = null;
            try {
                skrskr = KeyManager.getSecretKeyRing(encryptedData.getKeyID());
                final Iterator<PGPSecretKey> secretKeys = skrskr.getSecretKeys();
                this.secretMasterKey = secretKeys.next();
                this.secretSubkey = secretKeys.next();

            } catch (PGPException e) {
                throw new KeyNotFoundException(encryptedData.getKeyID());
            }

            if (KeyManager.isEncrypted(this.secretSubkey))
                throw new PassphraseRequiredException(this.secretMasterKey.getKeyID());
            this.decrypt();
            return;
        }
        if (this.currObject instanceof PGPCompressedData)
            this.decompress();
        if (this.currObject instanceof PGPOnePassSignatureList)
            this.verify();
        else
            throw new InvalidFileFormatException();
    }

    public void decrypt() throws PassphraseRequiredException, InvalidPassprhaseException, IOException, PGPException, InvalidFileFormatException {
        if (KeyManager.isEncrypted(this.secretSubkey) && this.passphrase == null)
            throw new PassphraseRequiredException(this.secretMasterKey.getKeyID());

        PGPPrivateKey privateKey = null;
        try {
            privateKey = KeyManager.extractPrivateKey(this.secretSubkey, this.passphrase);
        } catch (PGPException e) {
            throw new InvalidPassprhaseException(this.secretMasterKey.getKeyID());
        }

        // Decryption
        InputStream plainStream  = this.encryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));
        this.objectFactory = new PGPObjectFactory(plainStream, KEY_FINGERPRINT_CALCULATOR);
        this.currObject = objectFactory.nextObject();

        if (this.currObject instanceof PGPCompressedData)
            this.decompress();

        if (this.currObject instanceof PGPOnePassSignatureList)
            this.verify();
        else if (this.currObject instanceof PGPLiteralData)
            this.read();
        else
            throw new InvalidFileFormatException();

    }

    private void decompress() throws IOException, PGPException {
        PGPCompressedData compressedData = (PGPCompressedData) this.currObject;
        this.objectFactory = new PGPObjectFactory(compressedData.getDataStream(), KEY_FINGERPRINT_CALCULATOR);
        this.currObject = this.objectFactory.nextObject();
    }

    private void read() throws IOException {
        InputStream literalStream = ((PGPLiteralData)currObject).getInputStream();
        int ch;
        while ((ch = literalStream.read()) >= 0)
            this.status.appendMessage((char)ch);

        System.out.println(this.status.stringBuilder);
    }

    private void verify() {
        this.status.setVerificationApplied(true);
//        this.status.setVerificationDate();  ... file.getcreationdate()
        this.status.setDecryptionSucceeded(true);
        PGPOnePassSignatureList signatureList = (PGPOnePassSignatureList) this.currObject;
        PGPOnePassSignature signature = signatureList.get(0);
        PGPPublicKey signerPublicKey = null;
        try {
            signerPublicKey = KeyManager.getPublicKey(signature.getKeyID());
        } catch (PGPException e) {
            // TODO SignerKey not found
            System.out.println("Unknown signer.");
        }

        try {

            signature.init(new BcPGPContentVerifierBuilderProvider(), signerPublicKey);

            PGPLiteralData literalData = null;
            literalData = (PGPLiteralData) objectFactory.nextObject();
            InputStream literalStream = literalData.getInputStream();
            int ch;
            while ((ch = literalStream.read()) >= 0) {
                signature.update((byte) ch);
                this.status.appendMessage((char)ch);
            }

            PGPSignature verificationSignature = ((PGPSignatureList) objectFactory.nextObject()).get(0);
            boolean verificationStatus = signature.verify(verificationSignature);
            System.out.println(verificationStatus);

        } catch (Exception e) {
            System.out.println("wut");
            // TODO Verification failed, throw some shit
            // Signature by -----> SignerKey
        }

    }

}
