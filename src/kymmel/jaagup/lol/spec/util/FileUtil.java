package kymmel.jaagup.lol.spec.util;

import java.io.*;
import java.net.URL;

public class FileUtil {

    public static byte[] readHttpBytes(String url) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream httpIn = new URL(url).openStream();
        byte[] buffer = new byte[1024];
        int n;

        while((n = httpIn.read(buffer)) != -1)
            baos.write(buffer, 0, n);

        httpIn.close();
        return baos.toByteArray();

    }

    public static String readHttpString(String url) throws IOException {

        return new String(readHttpBytes(url), "UTF-8");

    }

    public static void writeFile(String fileName, byte[] bytes) throws IOException {

        if(fileName.lastIndexOf("/") > 0)
            new File(fileName).getParentFile().mkdirs();

        FileOutputStream fileOut = new FileOutputStream(fileName);
        fileOut.write(bytes);
        fileOut.close();

    }

    public static void writeFile(String fileName, String data) throws IOException {

        writeFile(fileName, data.getBytes("UTF-8"));

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

}
