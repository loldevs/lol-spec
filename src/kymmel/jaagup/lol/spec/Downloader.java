package kymmel.jaagup.lol.spec;

import kymmel.jaagup.misc.IO;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Downloader implements Runnable {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InterruptedException {

        Spectator[] specs = Rest.getFeaturedGames("EUN1");

        for(Spectator spec : specs) {

            Thread thread = new Thread(new Downloader(spec), "Spectator " + spec.gameId);
            thread.start();

        }

    }

    protected Spectator spec;

    public Downloader(Spectator spec) {

        this.spec = spec;

    }

    @Override
    public void run() {

        try {

            int lastChunk = 0;
            int lastKeyFrame = 0;
            ChunkInfo chunkInfo;

            while(true) {

                chunkInfo = spec.getChunkInfo();

                if(chunkInfo.chunkId > lastChunk) {

                    System.out.println(spec.gameId + ": Downloading chunk " + chunkInfo.chunkId);

                    IO.writeFile(
                            "analysis/" + spec.platformId + "." + spec.gameId + "." + chunkInfo.chunkId + ".chunk",
                            spec.getChunk(chunkInfo.chunkId)
                    );

                    lastChunk = chunkInfo.chunkId;

                }

                if(chunkInfo.keyFrameId > lastKeyFrame) {

                    System.out.println(spec.gameId + ": Downloading keyframe " + chunkInfo.keyFrameId);

                    IO.writeFile(
                            "analysis/" + spec.platformId + "." + spec.gameId + "." + chunkInfo.keyFrameId + ".keyframe",
                            spec.getKeyFrame(chunkInfo.keyFrameId)
                    );

                    lastKeyFrame = chunkInfo.keyFrameId;

                }

                if(chunkInfo.isEndChunk) {

                    System.out.println(spec.gameId + ": Finished downloading.");
                    break;

                }

                System.out.println(spec.gameId + ": Sleeping for " + chunkInfo.timeNextChunk + " milliseconds");
                Thread.sleep(chunkInfo.timeNextChunk);

            }

        } catch(Exception e) {

            e.printStackTrace();

        }

    }

}
