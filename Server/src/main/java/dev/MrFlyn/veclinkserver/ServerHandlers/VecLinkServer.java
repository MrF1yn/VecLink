package dev.mrflyn.veclinkserver.ServerHandlers;

import dev.mrflyn.veclinkserver.Main;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Timer;
import java.util.TimerTask;

public class VecLinkServer extends Thread{
    public int port;
    public EventLoopGroup bossGroup;
    public EventLoopGroup workerGroup;
    private Timer timer;
    private TimerTask clientKeepAliveTask;

    public VecLinkServer(int port)
    {
        this.port = port;
        timer = new Timer("Timer");
    }
    @Override
    public void run()
    {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(4);
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            Main.log("VecLink Server online and bound to port: "+port+".",true);
            startClientKeepAliveTask();
            bootstrap.bind(port).sync().channel().closeFuture().sync();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void startClientKeepAliveTask(){
        if(clientKeepAliveTask!=null) return;

        clientKeepAliveTask = new TimerTask() {
            @Override
            public void run() {
                for(VecLinkClient c : ServerHandler.ClientsByName.values()){
                    if(System.currentTimeMillis()-c.getLastKeepAlive()>30000L){
                        Main.log("Didn't receive keep-alive from: "+c.getName()+".", true);
                        c.getChannel().close();
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(clientKeepAliveTask, 1L, 1000L);

    }
}
