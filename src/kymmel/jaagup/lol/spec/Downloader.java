package kymmel.jaagup.lol.spec;

import kymmel.jaagup.lol.spec.domain.ChunkInfo;
import kymmel.jaagup.lol.spec.factory.RestRepositoryFactory;
import kymmel.jaagup.lol.spec.services.RestService;
import kymmel.jaagup.lol.spec.services.SpectatorService;
import kymmel.jaagup.lol.spec.util.FileUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Downloader implements Runnable {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InterruptedException {

        RestService eune = (RestService)RestRepositoryFactory.getRestRepository("EUN1");

        SpectatorService[] specs = eune.getFeaturedGames();

        for(SpectatorService spec : specs) {

            Thread thread = new Thread(new Downloader(spec), "Spectator " + spec.getGameId());
            thread.start();

        }

        /*

        String key = "oq/oMQ3YzHdTmYMDgbPz4nURXDNNijx/";
        String gid = "1289497042";

        for(byte bait : Base64.decodeBase64(key))
            System.out.print(bait + " ");
        System.out.println();
        for(byte bait : gid.getBytes())
            System.out.print(bait + " ");

        */

    }

    protected SpectatorService spec;
    public Downloader(SpectatorService spec) {
        this.spec = spec;
    }

    @Override
    public void run() {

        try {

            int lastChunk = 0;
            int lastKeyFrame = 0;
            ChunkInfo chunkInfo;

            while(true) {

                chunkInfo = spec.getLastChunkInfo();

                if(chunkInfo.chunkId > lastChunk) {

                    System.out.println(spec.getGameId() + ": Downloading chunk " + chunkInfo.chunkId);

                    FileUtil.writeFile(
                            "analysis/chunks/" + spec.getPlatformId() + "." + spec.getGameId() + "." +
                                    chunkInfo.chunkId + ".chunk",
                            spec.getChunk(chunkInfo.chunkId)
                    );

                    lastChunk = chunkInfo.chunkId;

                }

                if(chunkInfo.keyFrameId > lastKeyFrame) {

                    System.out.println(spec.getGameId() + ": Downloading keyframe " + chunkInfo.keyFrameId);

                    FileUtil.writeFile(
                            "analysis/keyframes/" + spec.getPlatformId() + "." + spec.getGameId() + "." +
                                    chunkInfo.keyFrameId + ".keyframe",
                            spec.getKeyFrame(chunkInfo.keyFrameId)
                    );

                    lastKeyFrame = chunkInfo.keyFrameId;

                }

                if(chunkInfo.isEndChunk) {

                    System.out.println(spec.getGameId() + ": Finished downloading.");
                    break;

                }

                System.out.println(spec.getGameId() + ": Sleeping for " + chunkInfo.timeNextChunk + " milliseconds");
                Thread.sleep(chunkInfo.timeNextChunk);

            }

        } catch(Exception e) {

            e.printStackTrace();

        }

    }

}
