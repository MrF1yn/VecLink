package veclinkspigot;

import dev.MrFlyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.MrFlyn.veclink.ClientHandlers.PacketHandler;
import dev.MrFlyn.veclink.Main;
import dev.mrflyn.veclinkcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PacketHandlerSpigot implements PacketHandler {
ReentrantLock lock = new ReentrantLock();
    @Override
    public void clearCaches(){
        lock.lock();
        try {
            ConnectedVecLinkClient.clients = Collections.synchronizedList(new ArrayList<>());
            ConnectedVecLinkClient.CFC = new ConcurrentHashMap<>();
            ConnectedVecLinkClient.groups = Collections.synchronizedList(new ArrayList<>());
            VecLinkMainSpigot.plugin.groups = Collections.synchronizedList(new ArrayList<>());
            VecLinkMainSpigot.plugin.chatGroups = new ConcurrentHashMap<>();
        }finally {
            lock.unlock();
        }
    }


    @Override
    public void handlePayload(Object[] packet, ChannelHandlerContext ctx) {
        PacketType packetType = (PacketType)packet[0];
        switch (packetType){
            case S2C_REMOTE_CMD:
                String executor = (String) packet[1];
                String remoteCmd = (String) packet[2];
                Main.gi.log("Received Remote Command Execution request from VecLink Server.");
                Main.gi.log("Command: "+ remoteCmd);
                Main.gi.log("Executor: "+ (executor.equals("@c")?"console":executor));

                if(executor.equals("@c")){
                    Bukkit.getScheduler().runTask(VecLinkMainSpigot.plugin, () ->{
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), remoteCmd);
                    });
                    return;
                }
                Bukkit.getScheduler().runTask(VecLinkMainSpigot.plugin, () ->{
                    Player p = Bukkit.getPlayer(executor);
                    if(p!=null&&Bukkit.getOnlinePlayers().contains(p)){
                        p.performCommand(remoteCmd);
                        return;
                    }
                    Main.gi.log("Remote Command Executor: "+executor+", not found.");
                });
                break;
            case S2C_AUTH:
                if((boolean) packet[1]){
                    Main.gi.startKeepAliveTask();
                    List<String> groupsList = (List<String>)packet[2];
                    VecLinkMainSpigot.plugin.groups = Collections
                            .synchronizedList(groupsList);

                    Main.gi.log("Successfully authorised. Syncing info...");
                    if(VecLinkMainSpigot.plugin.config.getBoolean("chat-module.enabled")&& VecLinkMainSpigot.plugin.config.getBoolean("chat-module.sync-chats")){
                        Bukkit.getScheduler().runTaskAsynchronously(VecLinkMainSpigot.plugin, ()->{
                            Main.client.channel.writeAndFlush(PacketFormatterSpigot.chatSyncInstantiate());
                        });
                    }
                    for(Player p : Bukkit.getOnlinePlayers()){
                        UUID uuid = p.getUniqueId();
                        String name = p.getName();
                        int i = Bukkit.getOnlinePlayers().size();
                        Bukkit.getScheduler().runTaskAsynchronously(VecLinkMainSpigot.plugin, ()->{
                            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatPlayerInfoPacket(uuid,name, "ADD", i,
                                    VecLinkMainSpigot.plugin.isJoinable()));
                        });
                    }
                }
                break;
            case S2C_CLIENT_INFO:
                String action = (String) packet[1];
                String name = (String) packet[2];
                String clientType = (String) packet[3];
                Main.gi.log("Received Client Info from VecLink Server for Client: "+name+".");
                if(action.equals("ADD")){
                    ConnectedVecLinkClient.clients.add(name);
                    if(!name.equals(Main.config.getMainConfig().getString("client-id")))
                        new ConnectedVecLinkClient(name,clientType);
                }
                else if (action.equals("REMOVE")) {
                    ConnectedVecLinkClient.CFC.remove(name);
                    ConnectedVecLinkClient.clients.remove(name);
                }
                break;
            case S2C_GROUP_INFO:
                List<String> groups = (List<String>) packet[1];
                Main.gi.log("Received Group Info from VecLink Server.");
                ConnectedVecLinkClient.groups = groups;
                break;
            case S2C_CLIENT_INFO_FORWARD:
                String type = (String) packet[1];
                String clType = (String) packet[2];
                String clientName = (String) packet[3];
                List<String> groupsList = (List<String>) packet[4];
                if(!ConnectedVecLinkClient.CFC.containsKey(clientName))return;
                ConnectedVecLinkClient cfc = ConnectedVecLinkClient.CFC.get(clientName);
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
            case S2C_CHAT_GROUP_INIT:
                String gAction = (String) packet[1];
                String groupName = (String) packet[2];
                String chatFormat = (String) packet[3];
                if(gAction.equals("ADD")){
                    VecLinkMainSpigot.plugin.chatGroups.put(groupName, chatFormat);
                    for(Player p : Bukkit.getOnlinePlayers()){
                        VecLinkMainSpigot.plugin.playerChatGroupStatus.put(p, new ArrayList<>(VecLinkMainSpigot.plugin.chatGroups.keySet()));
                    }
                }
                else if (gAction.equals("REMOVE")) {
                    VecLinkMainSpigot.plugin.chatGroups.remove(groupName);
                    for(Player p : Bukkit.getOnlinePlayers()){
                        VecLinkMainSpigot.plugin.playerChatGroupStatus.put(p, new ArrayList<>(VecLinkMainSpigot.plugin.chatGroups.keySet()));
                    }
                }
                break;
            case S2C_CHAT:
                String from = (String) packet[1];
                String msg = (String) packet[2];
                if(!VecLinkMainSpigot.plugin.config.getBoolean("chat-module.echo")){
                    if(from.equals(Main.config.getMainConfig().getString("client-id")))return;
                }
                Bukkit.getScheduler().runTask(VecLinkMainSpigot.plugin, ()->{
                   for(Player p : Bukkit.getServer().getOnlinePlayers()){
                       p.sendMessage(msg);
                   }
                });
                break;
            case S2C_CHAT_GRP:
                String from1 = (String) packet[1];
                String sender1 = (String) packet[2];
                String grpName = (String) packet[3];
                String msg1 = (String) packet[4];
                Bukkit.getScheduler().runTask(VecLinkMainSpigot.plugin, () -> {
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if(p.hasPermission("veclink.chatgroup."+grpName)){
                            if(VecLinkMainSpigot.plugin.playerChatGroupStatus.get(p).contains(grpName)) {
                                p.sendMessage(msg1);
                            }
                        }
                    }
                });
                break;
            case S2C_PLAYER_INFO:
                String clientId = (String) packet[1];
                String pAction = (String) packet[2];
                String pName = (String) packet[3];
                String pUuid = (String) packet[4];
                if(!ConnectedVecLinkClient.CFC.containsKey(clientId))return;
                ConnectedVecLinkClient c = ConnectedVecLinkClient.CFC.get(clientId);
                c.onPlayerInfoReceive(pName,
                        UUID.fromString(pUuid),
                        pAction);
                break;
        }
    }

    public void test(String test){
        System.out.println("FALCON TEST");
    }
}
