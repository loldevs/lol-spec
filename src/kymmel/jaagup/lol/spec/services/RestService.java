package kymmel.jaagup.lol.spec.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kymmel.jaagup.lol.spec.factory.SpectatorRepositoryFactory;
import kymmel.jaagup.lol.spec.util.FileUtil;

import java.io.IOException;

public class RestService {

    protected String platformID,
                     baseUrl;

    public SpectatorService[] getFeaturedGames() throws IOException {
        if(platformID == null || baseUrl == null)
            return null;

        JsonObject gameRoot = new JsonParser().parse(FileUtil.readHttpString(
                baseUrl + "featured"
        )).getAsJsonObject();

        JsonArray gameList = gameRoot.getAsJsonArray("gameList");
        int n = gameList.size();

        SpectatorService[] spectators = new SpectatorService[n];
        int gameId;
        String gameEncryptionKey,
               gamePlatformId;

        for(int i = 0; i < n; i++) {
            JsonObject game = gameList.get(i).getAsJsonObject();
            gameId = game.get("gameId").getAsInt();
            gameEncryptionKey = game.get("observers").getAsJsonObject().get("encryptionKey").getAsString();
            gamePlatformId = game.get("platformId").getAsString();

            spectators[i] = (SpectatorService)SpectatorRepositoryFactory.getRestRepository(
                    gamePlatformId, gameId, gameEncryptionKey
            );
        }
        return spectators;
    }

    public String getPlatformID() {
        return platformID;
    }

    public void setPlatformID(String platformID) {
        this.platformID = platformID;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
