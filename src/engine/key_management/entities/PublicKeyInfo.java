package engine.key_management.entities;

import org.bouncycastle.openpgp.PGPPublicKey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublicKeyInfo extends KeyInfo {

    public PublicKeyInfo(PGPPublicKey pgpPublicKey) {
        super(pgpPublicKey.getKeyID(), pgpPublicKey.getUserIDs().next());
    }

}
