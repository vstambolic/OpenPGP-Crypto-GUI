package engine.encryption;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;


import engine.key_management.KeyManager;
import engine.key_management.entities.PublicKeyInfo;
import engine.key_management.entities.SecretKeyInfo;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jcajce.provider.asymmetric.ElGamal;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.*;
import org.bouncycastle.util.io.Streams;

import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    private static OutputStream Encrypt(OutputStream stream, PGPPublicKey publicKey, String algorithm){
        try{
            int alg = algorithm.equals("Triple-DES") ? PGPEncryptedData.TRIPLE_DES : PGPEncryptedData.CAST5;
            OutputStream encryptionOut = null;
            final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder((alg)).setWithIntegrityPacket(true)
                            .setSecureRandom(new SecureRandom()).setProvider("BC")
            );

            encryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));
            encryptionOut = encryptedDataGenerator.open(stream, new byte[16*1024]);
            return encryptionOut;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    private static OutputStream Compress(OutputStream stream){
        try{
            OutputStream compressOut = null;
            final PGPCompressedDataGenerator compressedGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);

            compressOut = compressedGenerator.open(stream);
            compressedGenerator.close();
            return compressOut;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static PGPSignatureGenerator InitSignatureGenerator(PGPSecretKey secretKey, String password){
        try{
            PGPSignatureGenerator signatureGenerator = null;
            PGPSecretKey pgpSecKey = secretKey;
            PGPPrivateKey pgpPrivKey = null;
            try{
                pgpPrivKey = pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            signatureGenerator = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSecKey.getPublicKey().getAlgorithm(), PGPUtil.SHA256).setProvider("BC"));
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

            Iterator it = pgpSecKey.getPublicKey().getUserIDs();
            if (it.hasNext()) {
                PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

                spGen.setSignerUserID(false, ((String)it.next()).getBytes());
                signatureGenerator.setHashedSubpackets(spGen.generate());
            }

            return signatureGenerator;

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] Encrypt(final byte[] msg, String algorithm,PGPPublicKey publicKey, PGPSecretKey secretKey, String passphrase, String fileName, String savePath, boolean encrypt, boolean compress, boolean armoured, boolean sign){
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final ByteArrayInputStream in = new ByteArrayInputStream( msg );
            OutputStream encryptionOut = null;
            OutputStream compressOut = null;
            OutputStream mainStream = null;

            if(armoured){
                mainStream = new ArmoredOutputStream(byteArrayOutputStream);
            }else {
                mainStream = byteArrayOutputStream;
            }

            if(encrypt){
                encryptionOut = Encrypt(mainStream, publicKey, algorithm);
            }

            if(compress){
                if(encrypt){
                    compressOut = Compress(encryptionOut);
                }else{
                    compressOut = Compress(mainStream);
                }
            }

            PGPSignatureGenerator signatureGenerator = null;
            if (sign)
            {
                signatureGenerator = InitSignatureGenerator(secretKey, passphrase);
                if  (compress)
                    signatureGenerator.generateOnePassVersion(false).encode(compressOut);
                else if(encrypt)
                    signatureGenerator.generateOnePassVersion(false).encode(encryptionOut);
                else
                    signatureGenerator.generateOnePassVersion(false).encode(mainStream);
            }

            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            OutputStream literalOut = null;

            if (compress)
                literalOut = literalDataGenerator.open(compressOut, PGPLiteralData.BINARY, String.format("%s\\%s%s", savePath, fileName, ".gpg"), new Date(), new byte[4096]);
            else if(encrypt)
                literalOut = literalDataGenerator.open(encryptionOut, PGPLiteralData.BINARY, String.format("%s\\%s%s", savePath, fileName, ".gpg"), new Date(), new byte[4096]);
            else
                literalOut = literalDataGenerator.open(mainStream, PGPLiteralData.BINARY, String.format("%s\\%s%s", savePath, fileName, ".gpg"), new Date(), new byte[4096]);

            final byte[] buf = new byte[4096];
            for(int len = 0; (len = in.read(buf)) > 0;) {
                literalOut.write(buf, 0, len);
                if(sign)
                    signatureGenerator.update(buf, 0, len);
            }
            in.close();
            if(sign)
            {
                if(compress)
                    signatureGenerator.generate().encode(compressOut);
                else if(encrypt)
                    signatureGenerator.generate().encode(encryptionOut);
                else
                    signatureGenerator.generate().encode(mainStream);
            }
            literalOut.close();
            literalDataGenerator.close();

            if(compressOut!=null) compressOut.close();
            if(encryptionOut!=null) encryptionOut.close();

            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String Decrypt(FileInputStream fileInputStream) {
        try{
            InputStream in = fileInputStream;
            in = PGPUtil.getDecoderStream(in);

            JcaPGPObjectFactory pgpFactory = new JcaPGPObjectFactory(in);

            PGPEncryptedDataList encryptedDataList;

            Object message = pgpFactory.nextObject();
            while(message != null){
                if(message instanceof PGPEncryptedDataList){
                    encryptedDataList = (PGPEncryptedDataList) message;
                    Iterator it = encryptedDataList.getEncryptedDataObjects();
                    PGPPrivateKey sKey = null;
                    PGPPublicKeyEncryptedData pbe = null;

                    while (sKey == null && it.hasNext()) {
                        pbe = (PGPPublicKeyEncryptedData) it.next();
                        String password = null;
                        for(PublicKeyInfo info : KeyManager.getPublicKeyInfoCollection()){
                            if(info.getKeyId().equals(pbe.getKeyID())){
                                //Get password from ID
                            }
                        }

                        //String password = Keys.getPassword(pbe.getKeyID());
                        if (password == null) continue;
                        try {
                            PGPSecretKey pgpSecKey = null;
                            for(SecretKeyInfo info : KeyManager.getSecretKeyInfoCollection()){
                                if(info.getKeyId().equals(pbe.getKeyID())){
                                    // Get secret key from ID
                                }
                            }
                            if (pgpSecKey == null) return "Could not find the key.";
                            sKey = pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));

                        }catch (Exception e) {
                            return "Wrong password";
                        }
                    }
                    if (sKey == null) {
                        return "Decryption failed. Unknown receiver";
                    }
                    InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey));
                    pgpFactory = new JcaPGPObjectFactory(clear);
                    message = pgpFactory.nextObject();
                    System.out.println("Encryption successful\n");
                    continue;
                }

                if (message instanceof PGPCompressedData) {
                    PGPCompressedData cData = (PGPCompressedData) message;

                    pgpFactory = new JcaPGPObjectFactory(cData.getDataStream());
                    message = pgpFactory.nextObject();

                    System.out.println("Compression successful\n");
                    continue;
                }

                if (message instanceof PGPLiteralData) {
                    PGPLiteralData ld = (PGPLiteralData) message;

                    InputStream unc = ld.getInputStream();

                    Scanner s = new Scanner(unc).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "NO DATA AT ALL";
                    System.out.println("Message read");
                    return result;
                }

                if(message instanceof PGPOnePassSignatureList){
                    PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList) message;
                    PGPOnePassSignature signature = onePassSignatureList.get(0);

                    message = pgpFactory.nextObject();
                    PGPLiteralData literalData = (PGPLiteralData) message;

                    InputStream dIn = literalData.getInputStream();
                    int ch;


                    PGPPublicKey key = null;
                    for(SecretKeyInfo info : KeyManager.getSecretKeyInfoCollection()){
                        if(info.getKeyId().equals(signature.getKeyID())){
                            // Get public key from ID
                        }
                    }

                    signature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), key);
                    String result = "";
                    while ((ch = dIn.read()) >= 0) {
                        result = result + (char)ch;
                        signature.update((byte) ch);
                    }
                    message = pgpFactory.nextObject();
                    PGPSignatureList signatureList = (PGPSignatureList) message;

                    if (signature.verify(signatureList.get(0))) {
                        System.out.println("Signature verified by " + key.getUserIDs().next() + "\n");
                        System.out.println("signature verified.");
                        return result;
                    } else {
                        System.out.println("Verification error!\n");
                    }
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}

