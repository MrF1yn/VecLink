package dev.MrFlyn.FalconClient.ClientHandlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.MrFlyn.FalconClient.Main;
import dev.mrflyn.falconcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private FalconClient client;
    public ClientHandler(FalconClient client){
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object s) throws Exception {
        JsonObject json;

        try {

            json = JsonParser.parseString((String) s).getAsJsonObject();

        }
        catch (JsonSyntaxException e){
            Main.gi.log("Received bad data from: "+ctx.channel().remoteAddress());
            return;
        }

        Main.gi.debug(json);

        if(!json.has("type")){
            Main.gi.log("Received bad data from: "+ctx.channel().remoteAddress());
        }

        Main.pi.handlePayload(json, ctx);

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
                PacketType.C2S_AUTH,
                Main.config.getMainConfig().getString("client-id"),
                Main.config.getMainConfig().getString("secret-code"),
                Main.gi.getServerType()
        );
        ctx.writeAndFlush(packet.toArray());
        Main.gi.log("Sent Authentication details to FalconCloud Server.");
    }

}
