package kymmel.jaagup.lol.spec.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

public class CryptoUtil {

    public static byte[] decrypt(byte[] encrypted, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "Blowfish"));

        return cipher.doFinal(encrypted);

    }

    public static byte[] decompress(byte[] compressed) throws IOException {

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;

        while((n = gis.read(buffer)) != -1)
            baos.write(buffer, 0, n);

        return baos.toByteArray();

    }
}
