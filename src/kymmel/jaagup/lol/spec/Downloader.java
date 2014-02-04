package kymmel.jaagup.lol.spec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

public class Downloader {

    public static String BASE_URL = "http://spectator.eu.lol.riotgames.com:8088/observer-mode/rest/";

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InterruptedException {

        MetaData metaData = getFeaturedGames()[0];

        int lastChunk = -1;
        int lastKeyFrame = -1;
        ChunkInfo chunkInfo;

        while(true) {

             chunkInfo = getChunkInfo(metaData);

            if(chunkInfo.chunkId > lastChunk) {

                System.out.println("Downloading chunk " + chunkInfo.chunkId);

                writeFile(
                        metaData.platformId + "/" + metaData.gameId + "/" + chunkInfo.chunkId + ".chunk",
                        getChunk(metaData, chunkInfo.chunkId)
                );

                lastChunk = chunkInfo.chunkId;

            }

            if(chunkInfo.keyFrameId > lastKeyFrame) {

                System.out.println("Downloading keyframe " + chunkInfo.keyFrameId);

                writeFile(
                        metaData.platformId + "/" + metaData.gameId + "/" + chunkInfo.keyFrameId + ".keyframe",
                        getKeyFrame(metaData, chunkInfo.keyFrameId)
                );

                lastKeyFrame = chunkInfo.keyFrameId;

            }

            if(chunkInfo.isEndChunk)
                break;

            System.out.println("Sleeping for " + chunkInfo.timeNextChunk + " milliseconds");
            Thread.sleep(chunkInfo.timeNextChunk);

        }

    }

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

    public static byte[] getKeyFrame(MetaData metaData, int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = readHttpBytes(
                BASE_URL + "consumer/getKeyFrame/" + metaData.platformId + "/" + metaData.gameId + "/" + chunkId +
                "/token/"
        );

        byte[] realKey = decrypt(
                Base64.decodeBase64(metaData.encryptionKey),
                Integer.toString(metaData.gameId).getBytes()
        );

        return decompress(decrypt(encryptedChunk, realKey));

    }

    public static byte[] getChunk(MetaData metaData, int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = readHttpBytes(
                BASE_URL + "consumer/getGameDataChunk/" + metaData.platformId + "/" + metaData.gameId + "/" + chunkId +
                "/token/"
        );

        byte[] realKey = decrypt(
                Base64.decodeBase64(metaData.encryptionKey),
                Integer.toString(metaData.gameId).getBytes()
        );

        return decompress(decrypt(encryptedChunk, realKey));

    }

    public static MetaData[] getFeaturedGames() throws IOException {

        JsonObject gameRoot = new JsonParser().parse(readHttpString(
                BASE_URL + "featured"
        )).getAsJsonObject();

        JsonArray gameList = gameRoot.getAsJsonArray("gameList");
        int n = gameList.size();
        MetaData[] metaDatas = new MetaData[n];

        for(int i = 0; i < n; i++) {

            JsonObject game = gameList.get(i).getAsJsonObject();
            int gameId = game.get("gameId").getAsInt();
            String encryptionKey = game.get("observers").getAsJsonObject().get("encryptionKey").getAsString();
            String platformId = game.get("platformId").getAsString();

            metaDatas[i] = new MetaData(gameId, platformId, encryptionKey);

        }

        return metaDatas;

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

    public static ChunkInfo getChunkInfo(MetaData metaData) throws IOException {

        JsonObject chunkRoot = new JsonParser().parse(readHttpString(
                BASE_URL + "consumer/getLastChunkInfo/" + metaData.platformId + "/" + metaData.gameId + "/1/token/"
        )).getAsJsonObject();

        int chunkId = chunkRoot.get("chunkId").getAsInt();
        int timeNextChunk = chunkRoot.get("nextAvailableChunk").getAsInt();
        int keyFrameId = chunkRoot.get("keyFrameId").getAsInt();
        int endChunk = chunkRoot.get("endGameChunkId").getAsInt();

        return new ChunkInfo(chunkId, timeNextChunk, keyFrameId, endChunk);

    }

}
