package dev.MrFlyn.FalconServer.ServerHandlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.MrFlyn.FalconServer.Main;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Timer;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    public static ChannelGroup unAuthorisedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static ChannelGroup AuthorisedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static HashMap<String, FalconClient> ClientsByName = new HashMap();
    public static HashMap<Channel, String> NameByChannels = new HashMap();
    public static HashMap<String, ChannelGroup> ChannelsByGroups = new HashMap();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object s) throws Exception {
//        System.out.println("READ");
        JsonObject json;
        try {
            json = JsonParser.parseString((String) s).getAsJsonObject();
        }
        catch (JsonSyntaxException e){
            Main.log("Received bad data from: "+ctx.channel().remoteAddress(), false);
            return;
        }

        if(!json.has("type")){
            Main.log("Received bad data from: "+ctx.channel().remoteAddress(), false);
            return;
        }

        Main.debug(json, false);
        Channel c = ctx.channel();
        if(ServerHandler.unAuthorisedClients.contains(c)){
            if(!(json.has("type")&& json.has("name") && json.has("code") && json.has("server-type"))){
                Main.log("Received bad authorization data from "+c.remoteAddress()+". Closing connection...", false);
                ctx.close();
                return;
            }
            String name = json.get("name").getAsString();
            if(json.get("type").getAsString().equals("AUTH")){
                if(json.get("code").getAsString().equals(Main.config.getMainConfig().getString("secret-code"))){
                    if(ServerHandler.ClientsByName.containsKey(name)){
                        ctx.close();
                        Main.log("Name: "+name+" conflicts with already connected client. Closing connection...", false);
                        return;
                    }
                    unAuthorisedClients.remove(c);
                    AuthorisedClients.add(c);

                    FalconClient client = new FalconClient(name, c, ServerType.valueOf(json.get("server-type").getAsString()));
                    ClientsByName.put(name, client);
                    NameByChannels.put(c, name);
                    c.writeAndFlush(PacketFormatter.authStatus(true, client.getGroups())+"\n");
                    Main.log("Successfully authorised: "+c.remoteAddress()+" with name: "+name+".", false);
                    return;
                }
                Main.log("Received bad authorization data from "+c.remoteAddress()+". Closing connection...", false);
                ctx.close();
                return;
            }
            ctx.close();

        }
        else if(AuthorisedClients.contains(c)){
            PayloadHandler.handlePayload(ctx, json, ClientsByName.get(NameByChannels.get(ctx.channel())));
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String ip = ctx.channel().remoteAddress().toString();
        Main.debug("Handler Added for: "+ip+".", false);
        Main.log("Connection "+ip+" awaiting authorization.", false);
        unAuthorisedClients.add(ctx.channel());
        Timer t = new Timer("Timer");
        t.schedule(new DeAuthTask(ctx.channel()), 60000L);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Main.debug("Connection for "+ctx.channel().remoteAddress()+" dropped.", false);
        if(NameByChannels.containsKey(ctx.channel())){
            AuthorisedClients.writeAndFlush(PacketFormatter.formatClientInfoPacket(NameByChannels.get(ctx.channel()),
                    ClientsByName.get(NameByChannels.get(ctx.channel())).getType(), "REMOVE")+"\n");
            Main.log("Client "+ctx.channel().remoteAddress()+" with name: "+NameByChannels.get(ctx.channel())+" has disconnected.", false);
            ClientsByName.remove(NameByChannels.get(ctx.channel()));
            NameByChannels.remove(ctx.channel());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}