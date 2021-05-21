package engine.key_management.entities;


import engine.key_management.KeyManager;

import java.security.Key;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KeyInfo {
    private static Pattern pattern = Pattern.compile("^(.*)<(.*)>$", Pattern.CASE_INSENSITIVE);

    private long keyId;
    private String username;
    private String email;

    public KeyInfo(long keyId, String userId) {
        this.setKeyId(keyId);
        this.setUserInfo(userId);
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getKeyId() {
        // Long.toHexString(this.keyId).toUpperCase();
        return formatKeyId(keyId);
    }

    public long getKeyIdLong() {
        return this.keyId;
    }

    protected void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected void setUserInfo(String userId) {
        Matcher matcher = pattern.matcher(userId);
        if (matcher.find()) {
            this.setUsername(matcher.group(1));
            this.setEmail(matcher.group(2));
        }
        else {
            this.setUsername(userId);
            this.setEmail("");
        }
    }

    @Override
    public String toString() {
        return username + "[0x" + formatKeyId(keyId).substring(8)+ "]";
    }



    private static String formatKeyId(long keyId) {
        return String.format("%016X", keyId);
    }


}
