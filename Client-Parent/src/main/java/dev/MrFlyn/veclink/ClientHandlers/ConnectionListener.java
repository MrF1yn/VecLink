package dev.mrflyn.veclink.ClientHandlers;

import dev.mrflyn.veclink.Main;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

public class ConnectionListener implements ChannelFutureListener {
    private VecLinkClient client;
    public ConnectionListener(VecLinkClient client) {
        this.client = client;
    }
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
//            Main.gi.log("Connection Dropped. Reconnecting...");
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.createBootstrap(loop);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 3L, TimeUnit.SECONDS);
        }
        else if ((channelFuture.isSuccess())){
            Main.gi.log("Connected to VecLink Server at: " + client.ip + ":" + client.port + ".");
            client.channel = channelFuture.channel();
        }
    }
}
