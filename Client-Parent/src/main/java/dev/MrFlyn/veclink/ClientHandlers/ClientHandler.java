package dev.mrflyn.veclink.ClientHandlers;

import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclink.databases.MySQL;
import dev.mrflyn.veclink.databases.PostgreSQL;
import dev.mrflyn.veclinkcommon.ClientType;
import dev.mrflyn.veclinkcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private VecLinkClient client;
    public ClientHandler(VecLinkClient client){
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object s) throws Exception {
        System.out.println("Received!");
        System.out.println(s.toString());
        if(!(s instanceof Object[])){
            Main.gi.log("Received bad data from: "+ctx.channel().remoteAddress());
            return;
        }
        Object[] packet = (Object[]) s;
        Main.gi.debug(Arrays.toString(packet));

        if(!PacketType.validatePacket(packet, ClientType.SERVER)){
            Main.gi.log("Received bad data from: "+ctx.channel().remoteAddress());
            return;
        }
        Main.gi.debug(PacketType.values()[(int)packet[0]].name());

        if((int)packet[0]==PacketType.S2C_DATABASE_INFO.ordinal()){
            if(Main.db!=null){
                Main.db.disconnect();
            }
            String dbType = (String) packet[1];
            String host = (String) packet[2];
            String database = (String) packet[3];
            String user = (String) packet[4];
            String pass = (String) packet[5];
            int port = (int) packet[6];
            boolean ssl = (boolean) packet[7];
            boolean certificateVerification = (boolean) packet[8];
            int poolSize = (int) packet[9];
            int maxLifetime = (int) packet[10];

            switch (dbType) {
                case "MySQL":
                    Main.db = new MySQL(host,database,user,pass,port,ssl,certificateVerification,poolSize,maxLifetime);
                    Main.db.connect();
                    break;
                case "PostgreSQL":
                    Main.db = new PostgreSQL(host,database,user,pass,port,ssl,certificateVerification,poolSize,maxLifetime);
                    Main.db.connect();
                    break;
            }

        }

        Main.pi.handlePayload(packet, ctx);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ReadComplete");
//        ctx.flush();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Main.gi.log("Connection Dropped. "+(Main.client.shouldReconnect()?"Reconnecting...":""));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Main.gi.log("Channel Inactive. Communication dropped.");
        Main.pi.clearCaches();
        Main.gi.stopKeepAliveTask();
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    client.createBootstrap(eventLoop);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 3L, TimeUnit.SECONDS);
//        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        List<Object> packet = Arrays.asList(
                PacketType.C2S_AUTH.ordinal(),
                Main.config.getMainConfig().getString("client-id"),
                Main.config.getMainConfig().getString("secret-code"),
                Main.gi.getServerType()
        );
        ctx.writeAndFlush(packet.toArray());
        Main.gi.log("Sent Authentication details to VecLink Server.");
    }

}
