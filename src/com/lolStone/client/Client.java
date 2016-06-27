package com.lolStone.client;

import com.lolStone.client.model.Loggs;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;


/**
 * Created by root on 08.02.16.
 */
public class Client {
    static Loggs _log = new Loggs();
    public static void start()
    {

        while (true) {
            EventLoopGroup group = new NioEventLoopGroup();
            _log.info("try Connect");
            try {
               // final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new SecureChatClientInitializer());

                ChannelFuture f = b.connect(Config.HOST, Config.PORT).sync();
                f.channel().closeFuture().sync();

            }catch (Exception e) {
                _log.info(e);
            } finally {
                // The connection is closed automatically on shutdown.
                group.shutdownGracefully();
            }
            try{Thread.sleep(Config.TIME_DELEY_RECCONECT);}catch (Exception e){_log.info(e);}
        }

    }
}
