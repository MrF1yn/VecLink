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
    public void handlePayload(JsonObject json, ChannelHandlerContext ctx) {
        switch (PacketType.valueOf(json.get("type").getAsString())){
            case S2C_REMOTE_CMD:
                String remoteCmd = json.get("command").getAsString();
                String executor = json.get("executor").getAsString();
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
                if(json.get("status").getAsBoolean()){
                    Main.gi.startKeepAliveTask();
                    String groupsListStr = json.get("groups").getAsString();
                    FalconMainVelocity.plugin.groups = Collections
                            .synchronizedList(Arrays.asList(groupsListStr.substring(1,groupsListStr.length()-1).split(",")));

                    Main.gi.log("Successfully authorised. Syncing info...");
                    if(FalconMainVelocity.plugin.config.getBoolean("chat-module.enabled")&& FalconMainVelocity.plugin.config.getBoolean("chat-module.sync-chats")){

                            Main.client.channel.writeAndFlush(PacketFormatterVelocity.chatSyncInstantiate()+"\n");
                    }
                    for(Player p1 : FalconMainVelocity.plugin.server.getAllPlayers()){
                        UUID uuid = p1.getUniqueId();
                        String name = p1.getUsername();
                        int i = FalconMainVelocity.plugin.server.getPlayerCount();
                            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatPlayerInfoPacket(uuid,name, "ADD", i,
                                    FalconMainVelocity.plugin.isJoinable())+"\n");

                    }
                }
                break;
            case S2C_CLIENT_INFO:
                String name = json.get("name").getAsString();
                String clientType = json.get("client-type").getAsString();
                Main.gi.log("Received Client Info from FalconCloud Server for Client: "+name+".");
                if(json.get("action").getAsString().equals("ADD")){
                    ConnectedFalconClient.clients.add(name);
                    if(!name.equals(Main.config.getMainConfig().getString("client-id")))
                        new ConnectedFalconClient(name,clientType);
                }
                else if (json.get("action").getAsString().equals("REMOVE")) {
                    ConnectedFalconClient.CFC.remove(name);
                    ConnectedFalconClient.clients.remove(name);
                }
                break;
            case S2C_GROUP_INFO:
                String listString = json.get("group-list").getAsString();
                Main.gi.log("Received Group Info from FalconCloud Server.");
                ConnectedFalconClient.groups = Arrays.asList(listString.substring(1,listString.length()-1).split(","));
                break;
            case S2C_CLIENT_INFO_FORWARD:
                String type = json.get("sub-type").getAsString();
                String clientName = json.get("name").getAsString();
                String clType = json.get("client-type").getAsString();
                String groupsListStr = json.get("groups").getAsString();
                if(!ConnectedFalconClient.CFC.containsKey(clientName))return;
                ConnectedFalconClient cfc = ConnectedFalconClient.CFC.get(clientName);
                cfc.setClientType(clType);
                cfc.setGroups(Arrays.asList(groupsListStr.substring(1,groupsListStr.length()-1).split(",")));
                if(type.equals("BASIC")){
                    int playerCount = json.get("player-count").getAsInt();
                    boolean canJoin = json.get("can-join").getAsBoolean();
                    cfc.setOnlinePlayerCount(playerCount);
                    cfc.setCanJoin(canJoin);
                    return;
                }
                else if(type.equals("ADVANCED")){
//                    Arrays.asList(client.getLastKeepAliveInSecs(),client.getRunningThreads(),client.getCpuCores(),client.getCpuUsagePercent(),
//                            client.getMemoryUsagePercent(),client.getCurrentMemoryUsage(),client.getMaxMemory(),client.getAllocatedMemory()).toString());
                    String tpsListStr = json.get("tps").getAsString();
                    boolean canJoin = json.get("can-join").getAsBoolean();
                    String memInfoListStr = json.get("memory-info").getAsString();
                    double mspt = json.get("mspt").getAsDouble();
                    String osName = json.get("os-name").getAsString();
                    List<Double> tps = Arrays.asList(tpsListStr.substring(1,tpsListStr.length()-1).split(",")).stream()
                            .map(s->Double.parseDouble(s.trim())).collect(Collectors.toList());
                    List<Long> memoryInfo = Arrays.asList(memInfoListStr.substring(1,memInfoListStr.length()-1).split(",")).stream()
                            .map(s->Long.parseLong(s.trim())).collect(Collectors.toList());
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
                        String srv = json.get("backend-servers").getAsString();
                        cfc.setBackendServers(Arrays.asList(srv.substring(1,srv.length()-1).split(",")));
                    }
                    return;
                }
                break;
            case S2C_PLAYER_INFO:
                String clientId = json.get("client-id").getAsString();
                if(!ConnectedFalconClient.CFC.containsKey(clientId))return;
                ConnectedFalconClient c = ConnectedFalconClient.CFC.get(clientId);
                c.onPlayerInfoReceive(json.get("name").getAsString(),
                        UUID.fromString(json.get("uuid").getAsString()),
                        json.get("action").getAsString());
                break;
            case S2C_PARTY_INVITE:
                String targetPlayerName = json.get("invited").getAsString();
                String owner = json.get("owner").getAsString();
                Optional<Player> player = FalconMainVelocity.plugin.server.getPlayer(targetPlayerName);
                if (!player.isPresent()){
                    //target player not present
                    //send error msg back to owner;
                    return;
                }
                //player found send invite message to player.
        }
    }

    public void test(String test){
        System.out.println("FALCON TEST");
    }
}
