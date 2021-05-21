package engine.key_management.entities;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;

public class SecretKeyInfo extends KeyInfo {

    public SecretKeyInfo(PGPSecretKey pgpSecretKey) {
        super(pgpSecretKey.getKeyID(), pgpSecretKey.getUserIDs().next());
    }


}
