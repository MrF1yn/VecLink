package dev.mrflyn.veclinkserver.ServerHandlers;

import dev.mrflyn.veclinkserver.Main;
import dev.mrflyn.veclinkcommon.VLPlayer;
import dev.mrflyn.veclinkcommon.ClientType;
import dev.mrflyn.veclinkserver.databases.MySQL;
import dev.mrflyn.veclinkserver.databases.PostgreSQL;
import io.netty.channel.Channel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VecLinkClient {
//    arr[0] = Thread.getAllStackTraces().keySet().size(); //running threads;
//    arr[1] = i; //no.of cpu cores
//    arr[2] = (long) getProcessCpuLoad(); //cpu load
//    arr[3] = Long.parseLong(decimalFormat.format(100.0D - l10 * 100.0D / l7)); // memory usage percentage
//    arr[4] = Long.parseLong(decimalFormat.format((l7 - l10) / 1024L / 1024L)); // current memory usage
//    arr[5] = Long.parseLong(decimalFormat.format(l7 / 1024L / 1024L)); //max memory
//    arr[6] = Long.parseLong(decimalFormat.format(l8 / 1024L / 1024L)); //allocated memory
    public static String dcChatMonitorFormat = "";
    private String clientName;
    private Channel clientChannel;
    private ClientType clientType;
    private List<String> clientGroups;
    private long lastKeepAlive;
    private long runningThreads;
    private long cpuCores;
    private long cpuUsagePercent;
    private long memoryUsagePercent;
    private long CurrentMemoryUsage;
    private long maxMemory;
    private long allocatedMemory;
    private double tps1min;
    private double tps5min;
    private double tps15min;
    private double mspt;
    private String osName;
    private boolean canJoin;
    private List<String> chatSyncTarget;
    private List<String> backendServers;

    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    public static void updateDiscordChatMonitoringFormat(){
        for(VecLinkClient c : ServerHandler.ClientsByName.values()){
            if (c.getType()==ClientType.DISCORD_SRV&&c.getType()==ClientType.SERVER)continue;
            c.getChannel().writeAndFlush(PacketFormatter.formatDcChatMonitorInit(dcChatMonitorFormat));
        }
    }

    private int onlinePlayerCount;
    public HashMap<UUID, VLPlayer> playersByUuid;
    public HashMap<String, VLPlayer> playersByName;
    DecimalFormat decimalFormat;

    public long getLastKeepAliveInSecs(){
        return (System.currentTimeMillis()-lastKeepAlive/1000);
    }

    public VecLinkClient(String name, Channel channel, ClientType type){
        chatSyncTarget = new ArrayList<>();
        clientName = name;
        clientChannel = channel;
        clientType = type;
        clientGroups = new ArrayList<>();
        backendServers = new ArrayList<>();

        if(Main.db.isConnected()){
            if(Main.db instanceof MySQL){
                ((MySQL) Main.db).sendDbInfo(this);
            }
            if (Main.db instanceof PostgreSQL) {
                ((PostgreSQL) Main.db).sendDbInfo(this);
            }
        }

        for(String s : Main.config.getMainConfig().getConfigurationSection("groups").getKeys(false)){
            for(String l : Main.config.getMainConfig().getStringList("groups."+s)){
                if(l.equals(clientName))
                    clientGroups.add(s);
            }
        }

        for(String group : clientGroups){
            if(ServerHandler.ChannelsByGroups.containsKey(group)){
                ServerHandler.ChannelsByGroups.get(group).add(clientChannel);
            }
        }
        lastKeepAlive = System.currentTimeMillis();

        ServerHandler.AuthorisedClients.writeAndFlush(PacketFormatter.formatClientInfoPacket(name,type, "ADD"));
        for(VecLinkClient c : ServerHandler.ClientsByName.values()){
            if(!c.getName().equals(name)){
                channel.writeAndFlush(PacketFormatter.formatClientInfoPacket(c.getName(),c.getType(), "ADD"));
            }
        }
        ServerHandler.AuthorisedClients.writeAndFlush(PacketFormatter.formatGroupInfoPacket(ServerHandler.ChannelsByGroups.keySet()));
        decimalFormat = new DecimalFormat("0.00");
        playersByUuid = new HashMap<>();
        playersByName = new HashMap<>();
        onlinePlayerCount = 0;
//        ServerHandler.AuthorisedClients.writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(this,"BASIC")+"\n");
        for(VecLinkClient c : ServerHandler.ClientsByName.values()){
            if(!c.getName().equals(this.clientName)){
                c.getChannel().writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(this,"BASIC"));
                channel.writeAndFlush(PacketFormatter.formatClientInfoForwardPacket(c,"BASIC"));
                if(c.getType()!= ClientType.TEST) {
                    for (VLPlayer p : c.playersByName.values()) {
                        channel.writeAndFlush(PacketFormatter.formatPlayerInfoForward(p.getName(), p.getUUID().toString(), c.getName(), "ADD"));
                    }
                }
            }
        }

        for(String cg: Main.config.getMainConfig().getConfigurationSection("chat-groups").getKeys(false)){
            String format = Main.config.getMainConfig().getString("chat-groups."+cg+".format");
            channel.writeAndFlush(PacketFormatter.formatChatGroupInstantiatePacket(cg,format,"ADD"));
        }
    }

    public List<String> getFormattedClientInfo(){
        List<String> info = new ArrayList<>();
        info.add("ClientName: "+getName());
        info.add("\tIP: "+getChannel().remoteAddress().toString());
        info.add("\tClientType: "+getType());
        info.add("\tBelongingGroups: "+getGroups());
        info.add("\tIs-Joinable: "+isCanJoin());
        if(getType()!= ClientType.TEST) {
            info.add("\tTotalPlayers: "+onlinePlayerCount);
            if(getType()== ClientType.SPIGOT) {
                info.add("\tMspt: " + decimalFormat.format(getMspt()));
                info.add("\tTps 1m,5m,15m: " + decimalFormat.format(getTps1min()) + "," + decimalFormat.format(getTps5min()) + "," + decimalFormat.format(getTps15min()));
            }
        }
        info.add("\tOperatingSystem: "+getOsName());
        info.add("\tRunningThreads: "+getRunningThreads());
        info.add("\tNo.of CPU cores: "+getCpuCores());
        info.add("\tCpuUsage: "+decimalFormat.format(getCpuUsagePercent())+"%");
        info.add("\tMemoryUsage: "+decimalFormat.format(getMemoryUsagePercent())+"% ("
                +decimalFormat.format(getAllocatedMemory())+"/"+decimalFormat.format(getMaxMemory())+" MB)");
        info.add("\tAllocatedMemory: "+decimalFormat.format(getCurrentMemoryUsage())+"MB");
        return info;
    }

    public void onPlayerInfoReceive(String name, UUID uuid, String action, int onlinePlayerCount, boolean canJoin){
        if(clientType== ClientType.SPIGOT||clientType== ClientType.VELOCITY||clientType== ClientType.BUNGEE) {
            this.onlinePlayerCount = onlinePlayerCount;
            this.canJoin = canJoin;
            if (action.equals("ADD")) {
                VLPlayer p = new VLPlayer(name, uuid);
                playersByUuid.put(uuid, p);
                playersByName.put(name, p);
            } else if (action.equals("REMOVE")) {
                playersByUuid.remove(uuid);
                playersByName.remove(name);
            }
        }

    }


    public long getLastKeepAlive(){
        return lastKeepAlive;
    }

    public void setLastKeepAlive(long time){
        lastKeepAlive = time;
    }

    public ClientType getType() {
        return clientType;
    }

    public String getName() {
        return clientName;
    }

    public Channel getChannel() {
        return clientChannel;
    }

    public List<String> getGroups() {
        return clientGroups;
    }

    public double getMspt() {
        return mspt;
    }

    public void setMspt(double mspt) {
        this.mspt = mspt;
    }

    public long getRunningThreads() {
        return runningThreads;
    }

    public void setRunningThreads(long runningThreads) {
        this.runningThreads = runningThreads;
    }

    public long getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(long cpuCores) {
        this.cpuCores = cpuCores;
    }

    public long getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(long cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public long getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public void setMemoryUsagePercent(long memoryUsagePercent) {
        this.memoryUsagePercent = memoryUsagePercent;
    }

    public long getCurrentMemoryUsage() {
        return CurrentMemoryUsage;
    }

    public void setCurrentMemoryUsage(long currentMemoryUsage) {
        CurrentMemoryUsage = currentMemoryUsage;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(long allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }

    public double getTps1min() {
        return tps1min;
    }

    public void setTps1min(double tps1min) {
        this.tps1min = tps1min;
    }

    public double getTps5min() {
        return tps5min;
    }

    public void setTps5min(double tps5min) {
        this.tps5min = tps5min;
    }

    public double getTps15min() {
        return tps15min;
    }

    public void setTps15min(double tps15min) {
        this.tps15min = tps15min;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    public void setCanJoin(boolean canJoin) {
        this.canJoin = canJoin;
    }

    public void setChatSyncTargets(List<String> list){
        this.chatSyncTarget = list;
    }

    public List<String> getChatSyncTargets() {
        return this.chatSyncTarget;
    }

    public void setBackendServers(List<String> servers) {
        this.backendServers = servers;
    }

    public List<String> getBackendServers() {
        return this.backendServers;
    }

}
