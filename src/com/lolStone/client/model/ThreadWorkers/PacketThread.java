package com.lolStone.client.model.ThreadWorkers;

import ToClient.Packets.AddGameToCamera;
import ToClient.Packets.RemoveGameToCamera;
import ToClient.ToClientPacket;
import ToServer.Packets.Info;
import com.lolStone.client.model.Camera;
import com.lolStone.client.model.Loggs;
import model.GameInfo;

/**
 * Created by root on 10.02.16.
 */
public class PacketThread extends Thread {

    private boolean isStart = true;
    Loggs _log = new Loggs();

    public void Stop() {
        isStart = false;
    }

    @Override
    public void run() {
        ToClientPacket packet;
        while (isStart) {
            packet = Camera.Initialize().getPacket();
            if (packet == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    _log.info(e);
                }
                continue;
            }
            switch (packet.getType()) {
                case INFO:
                    Camera.Initialize().refresfInfo();
                    Camera.Initialize().getChanel().writeAndFlush(new Info(Camera.Initialize().getInfo()));
                    break;
                case ADD_GAME_TO_C:
                    GameInfo pa = ((AddGameToCamera) packet).gameInfo();
                    ThreadWorkAPI th = new ThreadWorkAPI(pa);
                    th.setName(pa.getKey());
                    Camera.Initialize().get_games().put(pa.getKey(), pa);
                    Camera.Initialize().getThreadAPI().put(th.getName(), th);
                    th.start();
                    break;
                case REMOVE_GAME_TO_C:
                    GameInfo re = ((RemoveGameToCamera) packet).gameInfo();
                    Camera.Initialize().get_games().remove(re.getKey());
                    ThreadWorkAPI t = Camera.Initialize().getThreadAPI().get(re.getKey());
                    if (t != null)
                        t.Stop();
                    Camera.Initialize().getThreadAPI().remove(re.getKey());
                    break;

            }
        }
    }
}
