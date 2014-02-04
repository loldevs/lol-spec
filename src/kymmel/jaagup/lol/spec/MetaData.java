package kymmel.jaagup.lol.spec;

public class MetaData {

    public int gameId;
    public String platformId;
    public String encryptionKey;

    public MetaData(int gameId, String platformId, String encryptionKey) {

        this.gameId = gameId;
        this.platformId = platformId;
        this.encryptionKey = encryptionKey;

    }

}
