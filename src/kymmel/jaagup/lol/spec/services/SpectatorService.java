package kymmel.jaagup.lol.spec.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kymmel.jaagup.lol.spec.domain.ChunkInfo;
import kymmel.jaagup.lol.spec.util.CryptoUtil;
import kymmel.jaagup.lol.spec.util.FileUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SpectatorService {

    protected int gameId;
    protected String platformId;
    protected String encryptionKey;
    protected String baseUrl;

    public byte[] getKeyFrame(int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = FileUtil.readHttpBytes(
                baseUrl + "consumer/getKeyFrame/" + platformId + "/" + gameId + "/" + chunkId +
                        "/token"
        );

        byte[] realKey = CryptoUtil.decrypt(
                Base64.decodeBase64(encryptionKey),
                Integer.toString(gameId).getBytes()
        );

        return CryptoUtil.decompress(CryptoUtil.decrypt(encryptedChunk, realKey));

    }

    public byte[] getChunk(int chunkId) throws IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedChunk = FileUtil.readHttpBytes(
                baseUrl + "consumer/getGameDataChunk/" + platformId + "/" + gameId + "/" + chunkId +
                        "/token"
        );

        byte[] realKey = CryptoUtil.decrypt(
                Base64.decodeBase64(encryptionKey),
                Integer.toString(gameId).getBytes()
        );

        return CryptoUtil.decompress(CryptoUtil.decrypt(encryptedChunk, realKey));

    }

    public ChunkInfo getLastChunkInfo() throws IOException {

        JsonObject chunkRoot = new JsonParser().parse(FileUtil.readHttpString(
                baseUrl + "consumer/getLastChunkInfo/" + platformId + "/" + gameId + "/1/token"
        )).getAsJsonObject();

        int chunkId = chunkRoot.get("chunkId").getAsInt();
        int timeNextChunk = chunkRoot.get("nextAvailableChunk").getAsInt();
        int keyFrameId = chunkRoot.get("keyFrameId").getAsInt();
        int endChunk = chunkRoot.get("endGameChunkId").getAsInt();

        return new ChunkInfo(chunkId, timeNextChunk, keyFrameId, endChunk);

    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
