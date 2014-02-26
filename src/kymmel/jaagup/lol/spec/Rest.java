package kymmel.jaagup.lol.spec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kymmel.jaagup.lol.spec.misc.IO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Rest {

    public static Map<String, String> servers = new HashMap<String, String>() {
        {
            put("NA1", "http://spectator.na.lol.riotgames.com:80/observer-mode/rest/");
            put("EUW1", "http://spectator.eu.lol.riotgames.com:8088/observer-mode/rest/");
            put("EUN1", "http://spectator.eu.lol.riotgames.com:8088/observer-mode/rest/");
            put("PBE1", "http://spectator.pbe1.lol.riotgames.com:8088/observer-mode/rest/");
        }
    };

    protected String platformID,
                     baseUrl;

    public Rest(String platformID, String baseUrl) {
        this.platformID = platformID;
        this.baseUrl = baseUrl;
    }

    public Spectator[] getFeaturedGames() throws IOException {

        JsonObject gameRoot = new JsonParser().parse(IO.readHttpString(
                baseUrl + "featured"
        )).getAsJsonObject();

        JsonArray gameList = gameRoot.getAsJsonArray("gameList");
        int n = gameList.size();

        Spectator[] spectators = new Spectator[n];
        int gameId;
        String gameEncryptionKey,
               gamePlatformId;

        for(int i = 0; i < n; i++) {

            JsonObject game = gameList.get(i).getAsJsonObject();
            gameId = game.get("gameId").getAsInt();
            gameEncryptionKey = game.get("observers").getAsJsonObject().get("encryptionKey").getAsString();
            gamePlatformId = game.get("platformId").getAsString();

            String server = (servers.containsKey(gamePlatformId) ? servers.get(gamePlatformId) : null);

            spectators[i] = new Spectator(gameId, gamePlatformId, gameEncryptionKey, server);

        }

        return spectators;

    }

}
