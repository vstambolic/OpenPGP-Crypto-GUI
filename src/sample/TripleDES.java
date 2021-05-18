package sample;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public class TripleDES {
    private static Cipher cipher1;
    private static Cipher cipher2;
    private static Cipher cipher3;

    static {

    }
    public static void main(String[] args) {
        try {
            cipher1 = Cipher.getInstance("DES");
//            cipher1.init(Cipher.ENCRYPT_MODE);
//            cipher2 = Cipher.getInstance("DES");
//            cipher3 = Cipher.getInstance("DES");


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }
}
