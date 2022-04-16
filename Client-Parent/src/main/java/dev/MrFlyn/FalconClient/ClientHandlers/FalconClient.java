package dev.MrFlyn.FalconClient.ClientHandlers;

import dev.MrFlyn.FalconClient.Main;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class FalconClient extends Thread {
    public int port;
    public String ip;
    public EventLoopGroup group;
    public Channel channel;
    private boolean reconnect;

    public boolean shouldReconnect(){
        return reconnect;
    }

    public void setReconnect(boolean t){
        this.reconnect = t;
    }

    public FalconClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        group = new NioEventLoopGroup();
        this.reconnect = true;
    }

    @Override
    public void run() {
        try {
            createBootstrap(group);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Bootstrap createBootstrap(EventLoopGroup eventLoop) throws InterruptedException {
        if(!reconnect)return null;
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer(this));
        Main.gi.log("Attempting to connect to FalconCloudServer.");
         bootstrap.connect(ip, port).addListener(new ConnectionListener(this));
//        channel = bootstrap.connect(ip, port).sync().channel();
        return bootstrap;
    }
}
