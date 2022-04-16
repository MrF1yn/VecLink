package dev.MrFlyn.FalconServer.ServerHandlers;

import com.google.gson.JsonObject;
import dev.MrFlyn.FalconServer.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PayloadHandler {

    public static void handlePayload(ChannelHandlerContext ctx, JsonObject json, FalconClient fromClient){
        Channel c = ctx.channel();
        switch (json.get("type").getAsString()){
            case "REMOTE-CMD":
                String target = json.get("target").getAsString();
                String executor = json.get("executor").getAsString();
                String command = json.get("command").getAsString();
                Main.log("Received Remote Command request from: "+ServerHandler.NameByChannels.get(c)+".", false);
                if(target.startsWith("group:")){
                    if(ServerHandler.ChannelsByGroups.containsKey(target.substring(6))){
                        ServerHandler.ChannelsByGroups.get(target.substring(6))
                                .writeAndFlush(PacketFormatter.formatRemoteCmdExec(executor, command)+"\n");
                        Main.log("Sent Remote Command execution to: "+target+".", false);
                        return;
                    }
                    Main.log("Received wrong group name from: "+ ServerHandler.NameByChannels.get(c)+".", false);
                    return;
                }

                if(ServerHandler.ClientsByName.containsKey(target)){
                    FalconClient client = ServerHandler.ClientsByName.get(target);
                    client.getChannel().writeAndFlush(PacketFormatter.formatRemoteCmdExec(executor, command)+"\n");
                    Main.log("Sent Remote Command execution to: "+target+".", false);
                    return;
                }
                Main.log("Received wrong target name from: "+ ServerHandler.NameByChannels.get(c)+".", false);
                break;
            case "KEEP-ALIVE":
                //Main.log("Received keep-alive from: "+fromClient.getName()+"."+fromClient.getType()+(fromClient.getType()==ServerType.SPIGOT), true);
                //    arr[0] = Thread.getAllStackTraces().keySet().size(); //running threads;
                //    arr[1] = i; //no.of cpu cores
                //    arr[2] = (long) getProcessCpuLoad(); //cpu load
                //    arr[3] = Long.parseLong(decimalFormat.format(100.0D - l10 * 100.0D / l7)); // memory usage percentage
                //    arr[4] = Long.parseLong(decimalFormat.format((l7 - l10) / 1024L / 1024L)); // current memory usage
                //    arr[5] = Long.parseLong(decimalFormat.format(l7 / 1024L / 1024L)); //max memory
                //    arr[6] = Long.parseLong(decimalFormat.format(l8 / 1024L / 1024L)); //allocated memory
                boolean joinStatus = json.get("joinable-status").getAsBoolean();
                String memInfo = json.get("memory-info").getAsString();
                String osName = json.get("os-name").getAsString();
//                    Arrays.asList(string.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
                List<Long> memoryInfo = Arrays.asList(memInfo.substring(1,memInfo.length()-1).split(",")).stream()
                        .map(s->Long.parseLong(s.trim())).collect(Collectors.toList());
                fromClient.setRunningThreads(memoryInfo.get(0));
                fromClient.setCpuCores(memoryInfo.get(1));
                fromClient.setCpuUsagePercent(memoryInfo.get(2));
                fromClient.setMemoryUsagePercent(memoryInfo.get(3));
                fromClient.setCurrentMemoryUsage(memoryInfo.get(4));
                fromClient.setMaxMemory(memoryInfo.get(5));
                fromClient.setAllocatedMemory(memoryInfo.get(6));
                fromClient.setCanJoin(joinStatus);
                fromClient.setOsName(osName);
                fromClient.setLastKeepAlive(System.currentTimeMillis());
                Main.debug("Received keep-alive from: "+fromClient.getName()+".", true);
                if(fromClient.getType()==ServerType.SPIGOT){
                    String tpsListString = json.get("tps").getAsString();
                    double mspt = json.get("mspt").getAsDouble();
                    List<Double> tps = Arrays.asList(tpsListString.substring(1,tpsListString.length()-1).split(",")).stream()
                            .map(s->Double.parseDouble(s.trim())).collect(Collectors.toList());
                    fromClient.setTps1min(tps.get(0));
                    fromClient.setTps5min(tps.get(1));
                    fromClient.setTps15min(tps.get(2));
                    fromClient.setMspt(mspt);
                }
                for(FalconClient cl : ServerHandler.ClientsByName.values()){
                    if(!cl.getName().equals(fromClient.getName())){
                        Main.debug("Forwarded client info to: "+cl.getName()+".", true);
                        cl.getChannel().writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(fromClient,"ADVANCED")+"\n");
                    }
                }
                break;
            case "PLAYER-INFO":
                String name = json.get("player-name").getAsString();
                UUID uuid = UUID.fromString(json.get("uuid").getAsString());
                String action = json.get("action").getAsString();
                int onlinePlayerCount = json.get("player-count").getAsInt();
                boolean canJoin = json.get("can-join").getAsBoolean();
                fromClient.onPlayerInfoReceive(name, uuid, action, onlinePlayerCount, canJoin);
                for(FalconClient cl : ServerHandler.ClientsByName.values()){
                    if(!cl.getName().equals(fromClient.getName())){
                        Main.debug("Forwarded client info to: "+cl.getName()+".", true);
                        cl.getChannel().writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(fromClient,"BASIC")+"\n");
                        Main.debug("Forwarded player-data info to: " + cl.getName() + ".", true);
                        cl.getChannel().writeAndFlush(PacketFormatter.formatPlayerInfoForward(name, uuid.toString(), fromClient.getName(), action) + "\n");
                    }
                }
                break;
            case "CHAT-SYNC-INSTANTIATE":
                String targets = json.get("target-servers").getAsString();
                List<String> list = Arrays.asList(targets.substring(1,targets.length()-1).split(", "));
                fromClient.setChatSyncTargets(list);
                break;
            case "CHAT":
                String msg = json.get("message").getAsString();
                JsonObject packet = PacketFormatter.formatChatDisplay(msg, fromClient.getName());
                for(String s : fromClient.getChatSyncTargets()){
                    if(!s.startsWith("group:")) {
                        FalconClient targetClient = ServerHandler.ClientsByName.get(s);
                        if(targetClient!=null)
                            targetClient.getChannel().writeAndFlush(packet+"\n");
                        continue;
                    }
                    String grpname = s.split(":")[1];
                    if(ServerHandler.ChannelsByGroups.containsKey(grpname))
                        ServerHandler.ChannelsByGroups.get(grpname).writeAndFlush(packet+"\n");
                }
                break;
            case "CHAT-GRP-MSG":
                String msg1 = json.get("message").getAsString();
                String grpName = json.get("group").getAsString();
                String from = json.get("from").getAsString();
                ServerHandler.AuthorisedClients.writeAndFlush(PacketFormatter.formatChatGrpDisplay(msg1,fromClient.getName(),grpName, from)+"\n");
                break;
        }
    }
}
