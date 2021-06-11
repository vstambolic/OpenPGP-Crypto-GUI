package engine.transfer.sender;

import engine.key_management.KeyManager;
import engine.key_management.entities.PublicKeyInfo;
import engine.key_management.entities.SecretKeyInfo;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

public class Sender {
    // Constraints
    private static final int BUFFER_SIZE = 1024;

    // Parameters ------------------------------------------------------
    private File file;
    private File outputDirectory;

    private boolean compressionEnabled;
    private boolean radix64Enabled;

    private boolean encryptEnabled;
    private int symmetricAlgorithmId;
    private PublicKeyInfo publicKeyInfo;

    private boolean signEnabled;
    private String passphrase;
    private SecretKeyInfo secretKeyInfo;

    public Sender(File file, File outputDirectory, boolean compressionEnabled, boolean radix64Enabled, boolean encryptEnabled, int symmetricAlgorithmId, PublicKeyInfo publicKeyInfo, boolean signEnabled, String passphrase, SecretKeyInfo secretKeyInfo) {
        this.file = file;
        this.outputDirectory = outputDirectory;
        this.compressionEnabled = compressionEnabled;
        this.radix64Enabled = radix64Enabled;

        if (this.encryptEnabled = encryptEnabled) {
            this.symmetricAlgorithmId = symmetricAlgorithmId;
            this.publicKeyInfo = publicKeyInfo;
        }
        if (this.signEnabled = signEnabled) {
            this.passphrase = passphrase;
            this.secretKeyInfo = secretKeyInfo;
        }
    }

    private String generateOutputFilePath() {
        return outputDirectory.getAbsolutePath() + '\\' + this.file.getName() + (this.encryptEnabled ? ".gpg" : ".sig");
    }


    public void send() throws FileNotFoundException, RuntimeException {
        // Prepare output ----------------------
        OutputStream targetOutStream = null;
        try {
            targetOutStream = new FileOutputStream(this.generateOutputFilePath());
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found error.");
        }
        try {

            // BACKWARDS SCHEME -------------------------------------------

            // Radix64 ----------------------------------------------------
            if (radix64Enabled)
                targetOutStream = new ArmoredOutputStream(targetOutStream);

            // Encryption -------------------------------------------------
            PGPEncryptedDataGenerator encryptedDataGenerator = null;
            OutputStream encryptOutStream = null;
            if (encryptEnabled) {
                if (this.publicKeyInfo == null)
                    throw new RuntimeException("Public key not provided.");
                encryptedDataGenerator = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(this.symmetricAlgorithmId).setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()).setProvider("BC"));
                encryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(KeyManager.getPublicSubkey(publicKeyInfo.getKeyIdLong())).setProvider("BC"));
                encryptOutStream = encryptedDataGenerator.open(targetOutStream, new byte[BUFFER_SIZE]);

            } else
                encryptOutStream = targetOutStream;

            // Compression -------------------------------------------------
            PGPCompressedDataGenerator compressedDataGenerator = null;
            OutputStream compressOutStream = null;
            if (compressionEnabled) {
                compressedDataGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
                compressOutStream = compressedDataGenerator.open(encryptOutStream);
            } else
                compressOutStream = encryptOutStream;

            // Signing ----------------------------------------------------
            PGPSignatureGenerator signatureGenerator = null;
            if (signEnabled) {
                if (this.secretKeyInfo == null)
                    throw new RuntimeException("Secret key not provided.");

                PGPSecretKey secretKey = KeyManager.getSecretKey(secretKeyInfo.getKeyIdLong());
                PGPPublicKey publicKey = secretKey.getPublicKey();
                PGPPrivateKey privateKey = null;

                try {
                    privateKey = KeyManager.extractPrivateKey(secretKey, this.passphrase);
                } catch (PGPException e) {
                    throw new RuntimeException("Invalid passphrase.");
                }

                signatureGenerator = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(publicKey.getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
                signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
                Iterator usersIterator = publicKey.getUserIDs();
                if (usersIterator.hasNext()) {  // Sign as single user
                    PGPSignatureSubpacketGenerator subpacketGenerator = new PGPSignatureSubpacketGenerator();
                    subpacketGenerator.setSignerUserID(false, (String) usersIterator.next());
                    signatureGenerator.setHashedSubpackets(subpacketGenerator.generate());
                }
                signatureGenerator.generateOnePassVersion(false).encode(compressOutStream);
            }

            // Write to file
            // Input
            FileInputStream inputStream = new FileInputStream(file);
            // Output
            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            OutputStream literalOut = literalDataGenerator.open(compressOutStream, PGPLiteralData.BINARY, this.file.getName(), new Date(), new byte[BUFFER_SIZE]);

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = inputStream.read(buffer, 0, buffer.length)) > 0) {
                literalOut.write(buffer, 0, len);
                if (signEnabled)
                    signatureGenerator.update(buffer, 0, len);
            }
            // close streams
            literalOut.close();
            literalDataGenerator.close();
            if (signEnabled)
                signatureGenerator.generate().encode(compressOutStream);

            compressOutStream.close();
            if (compressionEnabled)
                compressedDataGenerator.close();
            encryptOutStream.close();
            if (encryptEnabled)
                encryptedDataGenerator.close();
            if (radix64Enabled)
                targetOutStream.close();
        } catch (PGPException | IOException e) {
            throw new RuntimeException("Unexpected error occurred.");
        }
    }
}
