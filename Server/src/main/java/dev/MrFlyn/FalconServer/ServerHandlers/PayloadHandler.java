package dev.MrFlyn.FalconServer.ServerHandlers;

import com.google.gson.JsonObject;
import dev.MrFlyn.FalconServer.Main;
import dev.mrflyn.falconcommon.PacketType;
import dev.mrflyn.falconcommon.ClientType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PayloadHandler {

    public static void handlePayload(ChannelHandlerContext ctx, Object[] packet, FalconClient fromClient){
        Channel c = ctx.channel();
        PacketType packetType = (PacketType)packet[0];
        switch (packetType){
            case C2S_REMOTE_CMD:
                String target = (String) packet[1];
                String executor = (String) packet[2];
                String command = (String) packet[3];
                Main.log("Received Remote Command request from: "+ServerHandler.NameByChannels.get(c)+".", false);
                if(target.startsWith("group:")){
                    if(ServerHandler.ChannelsByGroups.containsKey(target.substring(6))){
                        ServerHandler.ChannelsByGroups.get(target.substring(6))
                                .writeAndFlush(PacketFormatter.formatRemoteCmdExec(executor, command));
                        Main.log("Sent Remote Command execution to: "+target+".", false);
                        return;
                    }
                    Main.log("Received wrong group name from: "+ ServerHandler.NameByChannels.get(c)+".", false);
                    return;
                }

                if(ServerHandler.ClientsByName.containsKey(target)){
                    FalconClient client = ServerHandler.ClientsByName.get(target);
                    client.getChannel().writeAndFlush(PacketFormatter.formatRemoteCmdExec(executor, command));
                    Main.log("Sent Remote Command execution to: "+target+".", false);
                    return;
                }
                Main.log("Received wrong target name from: "+ ServerHandler.NameByChannels.get(c)+".", false);
                break;
            case C2S_KEEP_ALIVE:
                //Main.log("Received keep-alive from: "+fromClient.getName()+"."+fromClient.getType()+(fromClient.getType()==ServerType.SPIGOT), true);
                //    arr[0] = Thread.getAllStackTraces().keySet().size(); //running threads;
                //    arr[1] = i; //no.of cpu cores
                //    arr[2] = (long) getProcessCpuLoad(); //cpu load
                //    arr[3] = Long.parseLong(decimalFormat.format(100.0D - l10 * 100.0D / l7)); // memory usage percentage
                //    arr[4] = Long.parseLong(decimalFormat.format((l7 - l10) / 1024L / 1024L)); // current memory usage
                //    arr[5] = Long.parseLong(decimalFormat.format(l7 / 1024L / 1024L)); //max memory
                //    arr[6] = Long.parseLong(decimalFormat.format(l8 / 1024L / 1024L)); //allocated memory
                boolean joinStatus = (boolean) packet[1];
                List<Long> memoryInfo = Arrays.asList((Long[])packet[2]);
                String osName = (String) packet[3];
//                    Arrays.asList(string.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
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
                if(fromClient.getType()== ClientType.SPIGOT){
                    List<Double> tps = Arrays.asList((Double[])packet[4]);
                    double mspt = (double) packet[5];

                    fromClient.setTps1min(tps.get(0));
                    fromClient.setTps5min(tps.get(1));
                    fromClient.setTps15min(tps.get(2));
                    fromClient.setMspt(mspt);
                }
                if(fromClient.getType()== ClientType.VELOCITY||fromClient.getType()== ClientType.BUNGEE){
                    List<String> backendServers = (List<String>)packet[4];
                    fromClient.setBackendServers(backendServers);
                }
                for(FalconClient cl : ServerHandler.ClientsByName.values()){
                    if(!cl.getName().equals(fromClient.getName())){
                        Main.debug("Forwarded client info to: "+cl.getName()+".", true);
                        cl.getChannel().writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(fromClient,"ADVANCED"));
                    }
                }
                break;
            case C2S_PLAYER_INFO:
                String action = (String) packet[1];
                UUID uuid = UUID.fromString((String) packet[2]);
                String name = (String) packet[3];
                int onlinePlayerCount = (int) packet[4];
                boolean canJoin = (boolean) packet[5];
                fromClient.onPlayerInfoReceive(name, uuid, action, onlinePlayerCount, canJoin);
                for(FalconClient cl : ServerHandler.ClientsByName.values()){
                    if(!cl.getName().equals(fromClient.getName())){
                        Main.debug("Forwarded client info to: "+cl.getName()+".", true);
                        cl.getChannel().writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(fromClient,"BASIC"));
                        Main.debug("Forwarded player-data info to: " + cl.getName() + ".", true);
                        cl.getChannel().writeAndFlush(PacketFormatter.formatPlayerInfoForward(name, uuid.toString(), fromClient.getName(), action));
                    }
                }
                break;
            case C2S_CHAT_SYNC_INIT:
                List<String> targets = (List<String>)packet[1];
                fromClient.setChatSyncTargets(targets);
                break;
            case C2S_CHAT:
                String msg = (String) packet[1];
                Object[] p1 = PacketFormatter.formatChatDisplay(msg, fromClient.getName());
                for(String s : fromClient.getChatSyncTargets()){
                    if(!s.startsWith("group:")) {
                        FalconClient targetClient = ServerHandler.ClientsByName.get(s);
                        if(targetClient!=null)
                            targetClient.getChannel().writeAndFlush(p1);
                        continue;
                    }
                    String grpname = s.split(":")[1];
                    if(ServerHandler.ChannelsByGroups.containsKey(grpname))
                        ServerHandler.ChannelsByGroups.get(grpname).writeAndFlush(p1);
                }
                break;
            case C2S_CHAT_GRP_MSG:
                String sender = (String) packet[1];
                String grpName = (String) packet[2];
                String msg1 = (String) packet[3];
                ServerHandler.AuthorisedClients.writeAndFlush(PacketFormatter.formatChatGrpDisplay(msg1,fromClient.getName(),grpName, sender));
                break;
        }
    }
}
