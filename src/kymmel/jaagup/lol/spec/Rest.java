package kymmel.jaagup.lol.spec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kymmel.jaagup.misc.IO;

import java.io.IOException;

public class Rest {

    public static Spectator[] getFeaturedGames(String platformId) throws IOException {

        String baseUrl = (Spectator.servers.containsKey(platformId) ? Spectator.servers.get(platformId) : null);

        JsonObject gameRoot = new JsonParser().parse(IO.readHttpString(
                baseUrl + "featured"
        )).getAsJsonObject();

        JsonArray gameList = gameRoot.getAsJsonArray("gameList");
        int n = gameList.size();

        Spectator[] spectators = new Spectator[n];
        int gameId;
        String encryptionKey;

        for(int i = 0; i < n; i++) {

            JsonObject game = gameList.get(i).getAsJsonObject();
            gameId = game.get("gameId").getAsInt();
            encryptionKey = game.get("observers").getAsJsonObject().get("encryptionKey").getAsString();
            platformId = game.get("platformId").getAsString();

            String server = (Spectator.servers.containsKey(platformId) ? Spectator.servers.get(platformId) : null);

            spectators[i] = new Spectator(gameId, platformId, encryptionKey, server);

        }

        return spectators;

    }

}
