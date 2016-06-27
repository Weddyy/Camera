package com.lolStone.client.model;

import model.GameChunk;
import org.json.JSONObject;

/**
 * Created by root on 04.02.16.
 */
public class JSONParser {

    public static GameChunk getGameInfo(String msg)
    {
        GameChunk c=new GameChunk();

        JSONObject obj = new JSONObject(msg);
        c.setChunkId(obj.getInt("chunkId"));
        c.setAvailableSince(obj.getInt("availableSince"));
        c.setNextAvailableChunk(obj.getInt("nextAvailableChunk"));
        c.setKeyFrameId(obj.getInt("keyFrameId"));
        c.setNextChunkId(obj.getInt("nextChunkId"));
        c.setEndStartupChunkId(obj.getInt("endStartupChunkId"));
        c.setStartGameChunkId(obj.getInt("startGameChunkId"));
        c.setEndGameChunkId(obj.getInt("endGameChunkId"));
        c.setDuration(obj.getInt("duration"));
        c.setJso(msg);
        return c;
    }
}
