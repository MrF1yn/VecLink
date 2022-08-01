package dev.MrFlyn.veclink.ClientHandlers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RemotePlayer {
//    public static ConcurrentHashMap<String, RemotePlayer> allRemotePlayers = new ConcurrentHashMap<>();
    ReentrantLock lock = new ReentrantLock();
    private UUID uuid;
    private String name;
    private List<String> connectedClients;

    public List<String> getConnectedClients() {
        lock.lock();
        try {
            return connectedClients;
        }finally {
            lock.unlock();
        }
    }

    public void setConnectedClients(List<String> connectedClients) {
        lock.lock();
        try {
            this.connectedClients = connectedClients;
        }
        finally {
            lock.unlock();
        }
    }

    public RemotePlayer(String playerName, UUID playerUUID){
        this.name = playerName;
        this.uuid = playerUUID;
        this.connectedClients = Collections.synchronizedList(new ArrayList<>());
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
