package com.lolStone.client.model.ThreadWorkers;

import com.lolStone.client.model.API.GameAPI;
import com.lolStone.client.model.Camera;
import com.lolStone.client.model.Loggs;
import enums.GameState;
import model.GameInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by root on 10.02.16.
 */
public class ThreadWorkAPI extends Thread {

    Loggs _log = new Loggs();
    private boolean isStart=true;
    private GameAPI api;

    public void Stop()
    {
        isStart=false;
    }
    private GameInfo _info;

    public ThreadWorkAPI(GameInfo info)
    {
        _info=info;
        api=new GameAPI(_info);
    }

    @Override
    public void run()
    {
        _info.setState(GameState.IN_CAMERA);
        int delay;
        try {
            while (!api.saveMeta()) { Thread.sleep(10000); }
            while (isStart) {
                delay = api.downloadGame();

                if(delay==-1)
                    break;
                Thread.sleep(delay);
            }
            _info.setState(GameState.GAME_END);
            api.clearTempDir();

        }catch ( Exception e)
        {
            _log.info(e);
            _info.setState(GameState.ERROR);
        }
        if(Camera.Initialize()!=null)
            Camera.Initialize().GameEND(_info);
    }
}
