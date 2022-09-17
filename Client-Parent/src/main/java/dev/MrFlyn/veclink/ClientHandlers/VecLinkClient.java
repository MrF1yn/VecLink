package dev.mrflyn.veclink.ClientHandlers;

import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclink.api.Monitor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VecLinkClient extends Thread {
    public int port;
    public String ip;
    public EventLoopGroup group;
    public Channel channel;
    private boolean reconnect;
    private List<Monitor> monitors;

    public boolean shouldReconnect(){
        return reconnect;
    }

    public void setReconnect(boolean t){
        this.reconnect = t;
    }

    public VecLinkClient(String ip, int port) {
        monitors = Collections.synchronizedList(new ArrayList<>());
        this.ip = ip;
        this.port = port;
        group = new NioEventLoopGroup();
        this.reconnect = true;
    }

    public void registerMonitors(Monitor m){
        monitors.add(m);
    }

    public void callMonitors(String clientName){
        for(Monitor m : monitors){
            m.onUpdate(clientName);
        }
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
        Main.gi.log("Attempting to connect to VecLinkServer.");
         bootstrap.connect(ip, port).addListener(new ConnectionListener(this));
//        channel = bootstrap.connect(ip, port).sync().channel();
        return bootstrap;
    }
}
