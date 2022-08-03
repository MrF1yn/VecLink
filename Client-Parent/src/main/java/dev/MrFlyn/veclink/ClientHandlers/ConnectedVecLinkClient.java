package dev.mrflyn.veclink.ClientHandlers;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectedVecLinkClient {
    public static List<String> clients = Collections.synchronizedList(new ArrayList<>());
    public static ConcurrentHashMap<String, ConnectedVecLinkClient> CFC = new ConcurrentHashMap<>();
    public static List<String> groups = Collections.synchronizedList(new ArrayList<>());

    private String clientName;
    private String clientType;
    private List<String> clientGroups;
    private List<String> backendServers;
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
    public ConcurrentHashMap<UUID, RemotePlayer> playersByUuid;
    public ConcurrentHashMap<String, RemotePlayer> playersByName;
    DecimalFormat decimalFormat;
    ReentrantLock lock = new ReentrantLock();


    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    public void setOnlinePlayerCount(int i){
        this.onlinePlayerCount = i;
    }

    private int onlinePlayerCount;

    public ConnectedVecLinkClient(String name, String type){
        clientName = name;
        clientType = type;
        backendServers = Collections.synchronizedList(new ArrayList<>());
        playersByUuid = new ConcurrentHashMap<>();
        playersByName = new ConcurrentHashMap<>();
        decimalFormat = new DecimalFormat("0.00");
        CFC.put(name, this);

    }
    public long getLastKeepAlive(){
        return lastKeepAlive;
    }

    public void setLastKeepAlive(long time){
        lastKeepAlive = time;
    }

    public String getType() {
        return clientType;
    }

    public String getName() {
        return clientName;
    }

    public List<String> getGroups() {
        return clientGroups;
    }

    public void setGroups(List<String> ls) {
        this.clientGroups = ls;
    }

    public double getMspt() {
        return mspt;
    }

    public void setClientType(String s){
        this.clientType=s;
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

    public void setBackendServers(List<String> srvs) {
        this.backendServers = srvs;
    }
    
    public List<String> getBackendServers() {
        return backendServers;
    }

    public List<String> getFormattedClientInfo(){
        List<String> info = new ArrayList<>();
        info.add("&bClientName: &f"+getName());
        info.add("&bClientType: &f"+getType());
        info.add("&bBelongingGroups: &f"+getGroups());
        info.add("&bIs-Joinable: &f"+isCanJoin());
        if(getType().equals("SPIGOT")) {
            info.add("&bTotalPlayers: &f"+onlinePlayerCount);
            info.add("&bMspt: &f" + decimalFormat.format(getMspt()));
            info.add("&bTps 1m,5m,15m: &f" + decimalFormat.format(getTps1min())+","+ decimalFormat.format(getTps5min())+","+ decimalFormat.format(getTps15min()));
        }else if(getType().equals("VELOCITY")||getType().equals("BUNGEE")){
            info.add("&bBackendServers: &f"+backendServers.toString());
        }
        info.add("&bOperatingSystem: &f"+getOsName());
        info.add("&bRunningThreads: &f"+getRunningThreads());
        info.add("&bNo.of CPU cores: &f"+getCpuCores());
        info.add("&bCpuUsage: &f"+decimalFormat.format(getCpuUsagePercent())+"%");
        info.add("&bMemoryUsage: &f"+decimalFormat.format(getMemoryUsagePercent())+"% ("
                +decimalFormat.format(getAllocatedMemory())+"/"+decimalFormat.format(getMaxMemory())+" MB)");
        info.add("&bAllocatedMemory: &f"+decimalFormat.format(getCurrentMemoryUsage())+"MB");
        return info;
    }

    public void onPlayerInfoReceive(String name, UUID uuid, String action){
        lock.lock();
        try {
            if (action.equals("ADD")) {
                RemotePlayer p = new RemotePlayer(name, uuid);
//                p.getConnectedClients().add(this.clientName);
//                RemotePlayer.allRemotePlayers.put(p.getName()+":"+p.getUUID().toString(), p);
                playersByUuid.put(uuid, p);
                playersByName.put(name, p);
            } else if (action.equals("REMOVE")) {
//                if(RemotePlayer.allRemotePlayers.containsKey(name+":"+uuid.toString())){
//                    RemotePlayer p = RemotePlayer.allRemotePlayers.get(name+":"+uuid.toString());
//                    p.getConnectedClients().remove(this.clientName);
//                    if(p.getConnectedClients().isEmpty()){
//                        RemotePlayer.allRemotePlayers.remove(name+":"+uuid.toString());
//                    }
//                }
                playersByUuid.remove(uuid);
                playersByName.remove(name);
            }
        }finally {
            lock.unlock();
        }
    }


}
