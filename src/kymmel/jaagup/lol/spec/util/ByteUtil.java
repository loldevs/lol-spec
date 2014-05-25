package kymmel.jaagup.lol.spec.util;

import kymmel.jaagup.lol.spec.exceptions.HexFormatException;

public class ByteUtil {

    protected static final char[] hexChars = "0123456789ABCDEF".toCharArray();

    public static String toHexString(byte b) {
        return toHexString(new byte[] {b});
    }

    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, false);
    }

    public static String toHexString(byte[] bytes, boolean spaced) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            int temp = b & 0xFF;
            sb.append(hexChars[temp >>> 4]);
            sb.append(hexChars[temp & 0xF]);
            if(spaced)
                sb.append(' ');
        }
        return sb.toString();
    }

    public static byte[] hexToByteArray(String str) {
        char[] chars = str.toCharArray();
        if((chars.length & 1) > 0)
            throw new HexFormatException();
        byte[] bytes = new byte[chars.length / 2];
        for(int i = 0; i < bytes.length; i++) {
            try {
                bytes[i] = (byte)((hexCharToByte(chars[i * 2]) << 4) | hexCharToByte(chars[i * 2 + 1]));
            } catch(HexFormatException e) {
                throw e;
            }
        }
        return bytes;
    }

    protected static byte hexCharToByte(char c) {
        for(byte i = 0; i < hexChars.length; i++)
            if(c == hexChars[i])
                return i;
        throw new HexFormatException();
    }

    public static boolean equals(byte[] a, byte[] b) {
        if(a.length != b.length)
            return false;
        for(int i = 0; i < a.length; i++)
            if(a[i] != b[i])
                return false;
        return true;
    }

}
