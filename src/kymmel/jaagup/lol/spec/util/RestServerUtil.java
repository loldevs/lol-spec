package kymmel.jaagup.lol.spec.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jaagup on 21.05.2014.
 */
public class RestServerUtil {
    public static Map<String, String> servers = new HashMap<String, String>() {
        {
            put("NA1", "http://spectator.na.lol.riotgames.com:80/observer-mode/rest/");
            put("EUW1", "http://spectator.eu.lol.riotgames.com:8088/observer-mode/rest/");
            put("EUN1", "http://spectator.eu.lol.riotgames.com:8088/observer-mode/rest/");
            put("PBE1", "http://spectator.pbe1.lol.riotgames.com:8088/observer-mode/rest/");
        }
    };
}
