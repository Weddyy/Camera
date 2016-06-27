package com.lolStone.client;

import ToServer.ToServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by root on 08.02.16.
 */
public class Encoder extends MessageToByteEncoder<Object> {
    public Encoder() {
    }

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {

        if(msg instanceof ToServerPacket)
            ToServerPacket.write((ToServerPacket) msg, buffer);
    }
}