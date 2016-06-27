package com.lolStone.client;

import ToClient.Packets.Login;
import ToClient.ToClientPacket;
import ToClient.eClientPackets;
import com.lolStone.client.model.Camera;
import com.lolStone.client.model.Loggs;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;

/**
 * Created by root on 08.02.16.
 */
public class SecureChatClientHandler extends SimpleChannelInboundHandler<ToClientPacket> {
    Loggs _log=new Loggs();
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new ToServer.Packets.LoginPacket().setIndex(8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ToClientPacket msg) {
        if(Camera.Initialize()==null) {
            if (msg.getType()== eClientPackets.LOGIN) {
                if (((Login) msg).isAllOk()) {
                    new Camera(ctx.channel());
                    _log.info("Connect to server.");
                }
            }
        }else
        {
            Camera.Initialize().addPacket(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        _log.info("Server disconnect");
        if(Camera.Initialize()!=null) Camera.Initialize().close();
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.shutdown();
        super.channelInactive(ctx);
    }
}