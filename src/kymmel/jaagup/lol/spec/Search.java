package kymmel.jaagup.lol.spec;

import java.io.*;
import java.util.ArrayList;

public class Search {

    public static final String FILE = "stuff";
    public static final String CHARSTR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-. ";

    public static void main(String[] args) throws IOException {

        byte[] chars = CHARSTR.getBytes();

        byte[] fileBytes = readFileBytes(FILE);
        ArrayList<String> results = new ArrayList<String>();
        ByteArrayOutputStream baos = null;
        int curLen = 0;

        for(int i = 0; i < fileBytes.length; i++) {

            boolean isASCII = contains(chars, fileBytes[i]);

            if(isASCII) {

                if(curLen == 0)
                    baos = new ByteArrayOutputStream();

                curLen++;
                baos.write(fileBytes[i]);

            } else {

                if(curLen > 3) {

                    results.add(baos.toString());
                    System.out.println(baos.toString());

                }

                curLen = 0;

            }

        }



    }

    public static byte[] readFileBytes(String file) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream httpIn = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        int n;

        while((n = httpIn.read(buffer)) != -1)
            baos.write(buffer, 0, n);

        httpIn.close();
        return baos.toByteArray();

    }

    public static boolean contains(byte[] array, byte v ) {
        for (byte e : array)
            if (e == v)
                return true;
        return false;
    }

}
