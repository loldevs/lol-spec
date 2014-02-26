package kymmel.jaagup.lol.spec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kymmel.jaagup.lol.spec.misc.Crypto;
import kymmel.jaagup.lol.spec.misc.IO;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Spectator {

    public int gameId;
    public String platformId;
    public String encryptionKey;
    public String server;

    public Spectator(int gameId, String platformId, String encryptionKey, String baseUrl) {

        this.gameId = gameId;
        this.platformId = platformId;
        this.encryptionKey = encryptionKey;
        this.server = baseUrl;

    }

    public byte[] getKeyFrame(int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = IO.readHttpBytes(
                server + "consumer/getKeyFrame/" + platformId + "/" + gameId + "/" + chunkId +
                        "/token"
        );

        byte[] realKey = Crypto.decrypt(
                Base64.decodeBase64(encryptionKey),
                Integer.toString(gameId).getBytes()
        );

        return Crypto.decompress(Crypto.decrypt(encryptedChunk, realKey));

    }

    public byte[] getChunk(int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = IO.readHttpBytes(
                server + "consumer/getGameDataChunk/" + platformId + "/" + gameId + "/" + chunkId +
                        "/token"
        );

        byte[] realKey = Crypto.decrypt(
                Base64.decodeBase64(encryptionKey),
                Integer.toString(gameId).getBytes()
        );

        return Crypto.decompress(Crypto.decrypt(encryptedChunk, realKey));

    }

    public ChunkInfo getChunkInfo() throws IOException {

        JsonObject chunkRoot = new JsonParser().parse(IO.readHttpString(
                server + "consumer/getLastChunkInfo/" + platformId + "/" + gameId + "/1/token"
        )).getAsJsonObject();

        int chunkId = chunkRoot.get("chunkId").getAsInt();
        int timeNextChunk = chunkRoot.get("nextAvailableChunk").getAsInt();
        int keyFrameId = chunkRoot.get("keyFrameId").getAsInt();
        int endChunk = chunkRoot.get("endGameChunkId").getAsInt();

        return new ChunkInfo(chunkId, timeNextChunk, keyFrameId, endChunk);

    }

}
