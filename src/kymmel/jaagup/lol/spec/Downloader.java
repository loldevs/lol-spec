package kymmel.jaagup.lol.spec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kymmel.jaagup.misc.Crypto;
import kymmel.jaagup.misc.IO;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

                IO.writeFile(
                        metaData.platformId + "/" + metaData.gameId + "/" + chunkInfo.chunkId + ".chunk",
                        getChunk(metaData, chunkInfo.chunkId)
                );

                lastChunk = chunkInfo.chunkId;

            }

            if(chunkInfo.keyFrameId > lastKeyFrame) {

                System.out.println("Downloading keyframe " + chunkInfo.keyFrameId);

                IO.writeFile(
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





    public static byte[] getKeyFrame(MetaData metaData, int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = IO.readHttpBytes(
                BASE_URL + "consumer/getKeyFrame/" + metaData.platformId + "/" + metaData.gameId + "/" + chunkId +
                        "/token/"
        );

        byte[] realKey = Crypto.decrypt(
                Base64.decodeBase64(metaData.encryptionKey),
                Integer.toString(metaData.gameId).getBytes()
        );

        return Crypto.decompress(Crypto.decrypt(encryptedChunk, realKey));

    }

    public static byte[] getChunk(MetaData metaData, int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = IO.readHttpBytes(
                BASE_URL + "consumer/getGameDataChunk/" + metaData.platformId + "/" + metaData.gameId + "/" + chunkId +
                        "/token/"
        );

        byte[] realKey = Crypto.decrypt(
                Base64.decodeBase64(metaData.encryptionKey),
                Integer.toString(metaData.gameId).getBytes()
        );

        return Crypto.decompress(Crypto.decrypt(encryptedChunk, realKey));

    }

    public static MetaData[] getFeaturedGames() throws IOException {

        JsonObject gameRoot = new JsonParser().parse(IO.readHttpString(
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

    public static ChunkInfo getChunkInfo(MetaData metaData) throws IOException {

        JsonObject chunkRoot = new JsonParser().parse(IO.readHttpString(
                BASE_URL + "consumer/getLastChunkInfo/" + metaData.platformId + "/" + metaData.gameId + "/1/token/"
        )).getAsJsonObject();

        int chunkId = chunkRoot.get("chunkId").getAsInt();
        int timeNextChunk = chunkRoot.get("nextAvailableChunk").getAsInt();
        int keyFrameId = chunkRoot.get("keyFrameId").getAsInt();
        int endChunk = chunkRoot.get("endGameChunkId").getAsInt();

        return new ChunkInfo(chunkId, timeNextChunk, keyFrameId, endChunk);

    }

}
