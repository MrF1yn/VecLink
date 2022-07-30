package FalconClientVelocity;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.ClientHandlers.PacketHandler;
import dev.MrFlyn.FalconClient.Main;
import dev.mrflyn.falconcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PacketHandlerVelocity implements PacketHandler {

    @Override
    public void clearCaches(){
        ConnectedFalconClient.clients= Collections.synchronizedList(new ArrayList<>());
        ConnectedFalconClient.CFC=new ConcurrentHashMap<>();
        ConnectedFalconClient.groups=Collections.synchronizedList(new ArrayList<>());
        FalconMainVelocity.plugin.groups = Collections.synchronizedList(new ArrayList<>());
    }


    @Override
    public void handlePayload(Object[] packet, ChannelHandlerContext ctx) {
        PacketType packetType = (PacketType)packet[0];
        switch (packetType){
            case S2C_REMOTE_CMD:
                String executor = (String) packet[1];
                String remoteCmd = (String) packet[2];
                Main.gi.log("Received Remote Command Execution request from FalconCloud Server.");
                Main.gi.log("Command: "+ remoteCmd);
                Main.gi.log("Executor: "+ (executor.equals("@c")?"console":executor));

                if (executor.equals("@c")) {
                    FalconMainVelocity.plugin.server.getCommandManager().executeAsync(FalconMainVelocity.plugin.server.getConsoleCommandSource(), remoteCmd);
                    return;
                }
                    Optional<Player> p = FalconMainVelocity.plugin.server.getPlayer(executor);
                    if(p.isPresent()){
                        FalconMainVelocity.plugin.server.getCommandManager().executeAsync(p.get(), remoteCmd);
                        return;
                    }
                    Main.gi.log("Remote Command Executor: "+executor+", not found.");
                break;
            case S2C_AUTH:
                if((boolean) packet[1]){
                    Main.gi.startKeepAliveTask();
                    List<String> groupsList = (List<String>)packet[2];
                    FalconMainVelocity.plugin.groups = Collections
                            .synchronizedList(groupsList);

                    Main.gi.log("Successfully authorised. Syncing info...");
                    if(FalconMainVelocity.plugin.config.getBoolean("chat-module.enabled")&& FalconMainVelocity.plugin.config.getBoolean("chat-module.sync-chats")){

                            Main.client.channel.writeAndFlush(PacketFormatterVelocity.chatSyncInstantiate());
                    }
                    for(Player p1 : FalconMainVelocity.plugin.server.getAllPlayers()){
                        UUID uuid = p1.getUniqueId();
                        String name = p1.getUsername();
                        int i = FalconMainVelocity.plugin.server.getPlayerCount();
                            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatPlayerInfoPacket(uuid,name, "ADD", i,
                                    FalconMainVelocity.plugin.isJoinable()));

                    }
                }
                break;
            case S2C_CLIENT_INFO:
                String action = (String) packet[1];
                String name = (String) packet[2];
                String clientType = (String) packet[3];
                Main.gi.log("Received Client Info from FalconCloud Server for Client: "+name+".");
                if(action.equals("ADD")){
                    ConnectedFalconClient.clients.add(name);
                    if(!name.equals(Main.config.getMainConfig().getString("client-id")))
                        new ConnectedFalconClient(name,clientType);
                }
                else if (action.equals("REMOVE")) {
                    ConnectedFalconClient.CFC.remove(name);
                    ConnectedFalconClient.clients.remove(name);
                }
                break;
            case S2C_GROUP_INFO:
                List<String> groups = (List<String>) packet[1];
                Main.gi.log("Received Group Info from FalconCloud Server.");
                ConnectedFalconClient.groups = groups;
                break;
            case S2C_CLIENT_INFO_FORWARD:
                String type = (String) packet[1];
                String clType = (String) packet[2];
                String clientName = (String) packet[3];
                List<String> groupsList = (List<String>) packet[4];
                if(!ConnectedFalconClient.CFC.containsKey(clientName))return;
                ConnectedFalconClient cfc = ConnectedFalconClient.CFC.get(clientName);
                cfc.setClientType(clType);
                cfc.setGroups(groupsList);
                if(type.equals("BASIC")){
                    int playerCount = (int) packet[5];
                    boolean canJoin = (boolean) packet[6];
                    cfc.setOnlinePlayerCount(playerCount);
                    cfc.setCanJoin(canJoin);
                    return;
                }
                else if(type.equals("ADVANCED")){
//                    Arrays.asList(client.getLastKeepAliveInSecs(),client.getRunningThreads(),client.getCpuCores(),client.getCpuUsagePercent(),
//                            client.getMemoryUsagePercent(),client.getCurrentMemoryUsage(),client.getMaxMemory(),client.getAllocatedMemory()).toString());
                    List<Double> tps = (List<Double>) packet[5];
                    boolean canJoin = (boolean) packet[6];
                    List<Long> memoryInfo = (List<Long>) packet[7];
                    double mspt = (double) packet[8];
                    String osName = (String) packet[9];
                    cfc.setLastKeepAlive(memoryInfo.get(0));
                    cfc.setRunningThreads(memoryInfo.get(1));
                    cfc.setCpuCores(memoryInfo.get(2));
                    cfc.setCpuUsagePercent(memoryInfo.get(3));
                    cfc.setMemoryUsagePercent(memoryInfo.get(4));
                    cfc.setCurrentMemoryUsage(memoryInfo.get(5));
                    cfc.setMaxMemory(memoryInfo.get(6));
                    cfc.setAllocatedMemory(memoryInfo.get(7));
                    cfc.setTps1min(tps.get(0));
                    cfc.setTps5min(tps.get(1));
                    cfc.setTps15min(tps.get(2));
                    cfc.setCanJoin(canJoin);
                    cfc.setOsName(osName);
                    cfc.setMspt(mspt);
                    if (cfc.getType().equals("VELOCITY")||cfc.getType().equals("BUNGEE")){
                        List<String> srv = (List<String>) packet[10];
                        cfc.setBackendServers(srv);
                    }
                    return;
                }
                break;
            case S2C_PLAYER_INFO:
                String clientId = (String) packet[1];
                String pAction = (String) packet[2];
                String pName = (String) packet[3];
                String pUuid = (String) packet[4];
                if(!ConnectedFalconClient.CFC.containsKey(clientId))return;
                ConnectedFalconClient c = ConnectedFalconClient.CFC.get(clientId);
                c.onPlayerInfoReceive(pName,
                        UUID.fromString(pUuid),
                        pAction);
                break;
//            case S2C_PARTY_INVITE:
//                String targetPlayerName = json.get("invited").getAsString();
//                String owner = json.get("owner").getAsString();
//                Optional<Player> player = FalconMainVelocity.plugin.server.getPlayer(targetPlayerName);
//                if (!player.isPresent()){
//                    //target player not present
//                    //send error msg back to owner;
//                    return;
//                }
//                //player found send invite message to player.
        }
    }

    public void test(String test){
        System.out.println("FALCON TEST");
    }
}
