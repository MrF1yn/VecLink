package dev.mrflyn.veclinkcommon;

import java.util.UUID;

public class VLPlayer {
    private UUID uuid;
    private String name;
    private String userID;
    private String userName;

    public VLPlayer(String playerName, UUID playerUUID){
        this.name = playerName;
        this.uuid = playerUUID;

    }

    public VLPlayer(UUID uuid, String name, String userID, String userName) {
        this.uuid = uuid;
        this.name = name;
        this.userID = userID;
        this.userName = userName;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
