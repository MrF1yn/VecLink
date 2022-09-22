package dev.mrflyn.veclinkserver.ServerHandlers;

import dev.mrflyn.veclinkserver.Main;
import dev.mrflyn.veclinkcommon.PacketType;
import dev.mrflyn.veclinkcommon.ClientType;
import dev.mrflyn.veclinkserver.Utils.DiscordVerificationHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PayloadHandler {

    public static void handlePayload(ChannelHandlerContext ctx, Object[] packet, VecLinkClient fromClient){
        Channel c = ctx.channel();
        PacketType packetType = PacketType.values()[(int)packet[0]];
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
                    VecLinkClient client = ServerHandler.ClientsByName.get(target);
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
                long[] memoryInfo = (long[])packet[2];
                String osName = (String) packet[3];
//                    Arrays.asList(string.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
                fromClient.setRunningThreads(memoryInfo[0]);
                fromClient.setCpuCores(memoryInfo[1]);
                fromClient.setCpuUsagePercent(memoryInfo[2]);
                fromClient.setMemoryUsagePercent(memoryInfo[3]);
                fromClient.setCurrentMemoryUsage(memoryInfo[4]);
                fromClient.setMaxMemory(memoryInfo[5]);
                fromClient.setAllocatedMemory(memoryInfo[6]);
                fromClient.setCanJoin(joinStatus);
                fromClient.setOsName(osName);
                fromClient.setLastKeepAlive(System.currentTimeMillis());
                Main.debug("Received keep-alive from: "+fromClient.getName()+".", true);
                if(fromClient.getType()== ClientType.SPIGOT){
                    double[] tps = (double[]) packet[4];
                    double mspt = (double) packet[5];

                    fromClient.setTps1min(tps[0]);
                    fromClient.setTps5min(tps[1]);
                    fromClient.setTps15min(tps[2]);
                    fromClient.setMspt(mspt);
                }
                if(fromClient.getType()== ClientType.VELOCITY||fromClient.getType()== ClientType.BUNGEE){
                    List<String> backendServers = (List<String>)packet[4];
                    fromClient.setBackendServers(backendServers);
                }
                for(VecLinkClient cl : ServerHandler.ClientsByName.values()){
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
                for(VecLinkClient cl : ServerHandler.ClientsByName.values()){
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
                        VecLinkClient targetClient = ServerHandler.ClientsByName.get(s);
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
            case C2S_DC_VERIFY_INIT:
                String pName = (String) packet[1];
                String pUUID = (String) packet[2];
                String token = Main.dvh.submitData(new DiscordVerificationHandler.VerificationData(pName, pUUID));
                fromClient.getChannel().writeAndFlush(PacketFormatter.formatDcVerifyInit(token,pName,pUUID));
                break;
            case C2S_DC_VERIFY_REQ:
                String uToken = (String) packet[1];
                String userID = (String) packet[2];
                String userName= (String) packet[3];
                DiscordVerificationHandler.VerificationData data= Main.dvh.verify(uToken, userName, userID);
                if(data!=null){
                    for(VecLinkClient vc : ServerHandler.ClientsByName.values()){
                        if(vc.getType()==ClientType.SPIGOT||vc.getType()==ClientType.DISCORD_SRV){
                            vc.getChannel().writeAndFlush(PacketFormatter.formatDcVerifyAck(true,data.playerName,data.playerUUID,data.userID,data.userName));
                        }
                    }
                }else{
                    fromClient.getChannel().writeAndFlush(PacketFormatter.formatDcVerifyAck(false,null,null,userID,userName));
                }
                break;
            case C2S_FIND_PLAYER:
                String targetPlayerName = (String) packet[1];
                String playerName = (String) packet[2];
                String targetServer = (String) packet[3];
                if(!ServerHandler.ClientsByName.containsKey(targetServer))return;
                ServerHandler.ClientsByName.get(targetServer).getChannel().writeAndFlush(PacketFormatter.formatFindPlayerPacket(targetPlayerName, playerName));
                break;
        }
    }
}
