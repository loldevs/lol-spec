package kymmel.jaagup.lol.spec.domain;

public class ChunkInfo {

    public int chunkId = 0;
    public int timeNextChunk = 0;
    public int keyFrameId = 0;
    public boolean isEndChunk = false;

    public ChunkInfo(int chunkId, int timeNextChunk, int keyFrameId, int endChunk) {

        this.chunkId = chunkId;
        this.timeNextChunk = timeNextChunk;
        this.keyFrameId = keyFrameId;
        this.isEndChunk = (endChunk > 0 && endChunk <= chunkId) ? true : false;

    }

}
