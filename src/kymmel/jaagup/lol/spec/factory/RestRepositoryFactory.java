package kymmel.jaagup.lol.spec.factory;

import kymmel.jaagup.lol.spec.services.RestService;
import kymmel.jaagup.lol.spec.util.RestServerUtil;

public class RestRepositoryFactory {
    public static RestService getRestRepository(String platformId) {
        if(RestServerUtil.servers.containsKey(platformId)) {
            RestService restService = new RestService();
            restService.setPlatformID(platformId);
            restService.setBaseUrl(RestServerUtil.servers.get(platformId));
            return restService;
        }
        return null;
    }
}
