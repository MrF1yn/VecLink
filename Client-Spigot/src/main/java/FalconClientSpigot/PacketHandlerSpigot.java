package FalconClientSpigot;

import com.google.gson.JsonObject;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.ClientHandlers.PacketHandler;
import dev.MrFlyn.FalconClient.Main;
import dev.mrflyn.falconcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class PacketHandlerSpigot implements PacketHandler {
ReentrantLock lock = new ReentrantLock();
    @Override
    public void clearCaches(){
        lock.lock();
        try {
            ConnectedFalconClient.clients = Collections.synchronizedList(new ArrayList<>());
            ConnectedFalconClient.CFC = new ConcurrentHashMap<>();
            ConnectedFalconClient.groups = Collections.synchronizedList(new ArrayList<>());
            FalconMainSpigot.plugin.groups = Collections.synchronizedList(new ArrayList<>());
            FalconMainSpigot.plugin.chatGroups = new ConcurrentHashMap<>();
        }finally {
            lock.unlock();
        }
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

                if(executor.equals("@c")){
                    Bukkit.getScheduler().runTask(FalconMainSpigot.plugin, () ->{
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), remoteCmd);
                    });
                    return;
                }
                Bukkit.getScheduler().runTask(FalconMainSpigot.plugin, () ->{
                    Player p = Bukkit.getPlayer(executor);
                    if(p!=null&&Bukkit.getOnlinePlayers().contains(p)){
                        p.performCommand(remoteCmd);
                        return;
                    }
                    Main.gi.log("Remote Command Executor: "+executor+", not found.");
                });
                break;
            case S2C_AUTH:
                if(json.get("status").getAsBoolean()){
                    Main.gi.startKeepAliveTask();
                    String groupsListStr = json.get("groups").getAsString();
                    FalconMainSpigot.plugin.groups = Collections
                            .synchronizedList(Arrays.asList(groupsListStr.substring(1,groupsListStr.length()-1).split(",")));

                    Main.gi.log("Successfully authorised. Syncing info...");
                    if(FalconMainSpigot.plugin.config.getBoolean("chat-module.enabled")&&FalconMainSpigot.plugin.config.getBoolean("chat-module.sync-chats")){
                        Bukkit.getScheduler().runTaskAsynchronously(FalconMainSpigot.plugin, ()->{
                            Main.client.channel.writeAndFlush(PacketFormatterSpigot.chatSyncInstantiate()+"\n");
                        });
                    }
                    for(Player p : Bukkit.getOnlinePlayers()){
                        UUID uuid = p.getUniqueId();
                        String name = p.getName();
                        int i = Bukkit.getOnlinePlayers().size();
                        Bukkit.getScheduler().runTaskAsynchronously(FalconMainSpigot.plugin, ()->{
                            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatPlayerInfoPacket(uuid,name, "ADD", i,
                                    FalconMainSpigot.plugin.isJoinable())+"\n");
                        });
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
            case S2C_CHAT_GROUP_INIT:
                String action = json.get("action").getAsString();
                String groupName = json.get("group-name").getAsString();
                String chatFormat = json.get("chat-format").getAsString();
                if(action.equals("ADD")){
                    FalconMainSpigot.plugin.chatGroups.put(groupName, chatFormat);
                    for(Player p : Bukkit.getOnlinePlayers()){
                        FalconMainSpigot.plugin.playerChatGroupStatus.put(p, new ArrayList<>(FalconMainSpigot.plugin.chatGroups.keySet()));
                    }
                }
                else if (action.equals("REMOVE")) {
                    FalconMainSpigot.plugin.chatGroups.remove(groupName);
                    for(Player p : Bukkit.getOnlinePlayers()){
                        FalconMainSpigot.plugin.playerChatGroupStatus.put(p, new ArrayList<>(FalconMainSpigot.plugin.chatGroups.keySet()));
                    }
                }
                break;
            case S2C_CHAT:
                String msg = json.get("message").getAsString();
                String from = json.get("from").getAsString();
                if(!FalconMainSpigot.plugin.config.getBoolean("chat-module.echo")){
                    if(from.equals(Main.config.getMainConfig().getString("client-id")))return;
                }
                Bukkit.getScheduler().runTask(FalconMainSpigot.plugin, ()->{
                   for(Player p : Bukkit.getServer().getOnlinePlayers()){
                       p.sendMessage(msg);
                   }
                });
                break;
            case S2C_CHAT_GRP:
                String msg1 = json.get("message").getAsString();
                String from1 = json.get("from").getAsString();
                String grpName = json.get("group").getAsString();
                Bukkit.getScheduler().runTask(FalconMainSpigot.plugin, () -> {
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if(p.hasPermission("falcon.chatgroup."+grpName)){
                            if(FalconMainSpigot.plugin.playerChatGroupStatus.get(p).contains(grpName)) {
                                p.sendMessage(msg1);
                            }
                        }
                    }
                });
                break;
            case S2C_PLAYER_INFO:
                String clientId = json.get("client-id").getAsString();
                if(!ConnectedFalconClient.CFC.containsKey(clientId))return;
                ConnectedFalconClient c = ConnectedFalconClient.CFC.get(clientId);
                c.onPlayerInfoReceive(json.get("name").getAsString(),
                        UUID.fromString(json.get("uuid").getAsString()),
                        json.get("action").getAsString());
                break;
        }
    }

    public void test(String test){
        System.out.println("FALCON TEST");
    }
}
