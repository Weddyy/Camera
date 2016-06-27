package com.lolStone.client;

import ToClient.ToClientPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by root on 08.02.16.
 */
public class Decoder extends ByteToMessageDecoder {
    public Decoder() {
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if (msg.readableBytes() % 1024 == 0)
            return;
        out.add(ToClientPacket.read(msg));
    }
}
