package com.lolStone.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * Created by root on 08.02.16.
 */
public class SecureChatClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
         ChannelPipeline pipeline = ch.pipeline();

        // pipeline.addLast(sslCtx.newHandler(ch.alloc(), Config.HOST, Config.PORT));

        pipeline.addLast(new Encoder());
        pipeline.addLast(new Decoder());

         pipeline.addLast(new SecureChatClientHandler());

    }
}
