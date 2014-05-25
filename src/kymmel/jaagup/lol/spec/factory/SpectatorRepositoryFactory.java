package kymmel.jaagup.lol.spec.factory;

import kymmel.jaagup.lol.spec.services.SpectatorService;
import kymmel.jaagup.lol.spec.util.RestServerUtil;

public class SpectatorRepositoryFactory {
    public static SpectatorService getRestRepository(String platformId, int gameId, String encryptionKey) {
        if(RestServerUtil.servers.containsKey(platformId)) {
            SpectatorService spectatorService = new SpectatorService();
            spectatorService.setPlatformId(platformId);
            spectatorService.setBaseUrl(RestServerUtil.servers.get(platformId));
            spectatorService.setGameId(gameId);
            spectatorService.setEncryptionKey(encryptionKey);
            return spectatorService;
        }
        return null;
    }
}
