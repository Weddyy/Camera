package com.lolStone.client.model;

import ToClient.ToClientPacket;
import ToServer.Packets.GameEnd;
import com.lolStone.client.ParseSystemInfo;
import com.lolStone.client.model.ThreadWorkers.PacketThread;
import com.lolStone.client.model.ThreadWorkers.ThreadWorkAPI;
import io.netty.channel.Channel;
import model.ClientInfo;
import model.GameInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 10.02.16.
 */
public class Camera {

    private static Camera thisModel;
    private ClientInfo info=new ClientInfo();
    private PacketThread packetThread;
    private Map<String,ThreadWorkAPI> threadAPI=new ConcurrentHashMap<>();
    Channel chanel;

    public List<ToClientPacket> _packets= Collections.synchronizedList(new LinkedList<ToClientPacket>());
    public Map<String, GameInfo> _games=new ConcurrentHashMap<>();

    public Camera(Channel c)
    {
        thisModel=this;
        chanel=c;
        packetThread=new PacketThread();
        packetThread.start();
    }

    public ClientInfo getInfo() {
        return info;
    }

    public Map<String,ThreadWorkAPI> getThreadAPI() {
        return threadAPI;
    }

    public Channel getChanel() {
        return chanel;
    }

    public Map<String, GameInfo> get_games() {
        return _games;
    }

    public void addPacket(ToClientPacket packet)
    {
        _packets.add(packet);
    }

    public ToClientPacket getPacket()
    {
        ToClientPacket p=null;
        if (_packets.size() != 0) {
            p = _packets.get(0);
            _packets.remove(0);
        }
        return p;
    }

    public void close()
    {
        if(packetThread!=null)
            packetThread.Stop();
        for (ThreadWorkAPI th:threadAPI.values())
            if(th!=null)
                th.Stop();
        _games.clear();
        _packets.clear();
        threadAPI.clear();
        thisModel=null;
    }

    public void GameEND(GameInfo info)
    {
        threadAPI.remove(info.getKey());
        _games.remove(info.getKey());
        GameEnd end=new GameEnd();
        end.setServerId(info.getServerId());
        end.setGameId(info.get_idGame());
        end.setLastChunk(info.getLastChunk());
        end.setState(info.getState().getValue());
        chanel.writeAndFlush(end);
    }

    public static Camera Initialize()
    {
        return thisModel;
    }

    public void refresfInfo()
    {
        ParseSystemInfo.refresh(info);
        info.setCountUsers(threadAPI.size());
    }
}