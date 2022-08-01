package dev.MrFlyn.veclinkserver.Utils;

import java.util.UUID;

public class Player {
    private UUID uuid;
    private String name;

    public Player(String playerName, UUID playerUUID){
        this.name = playerName;
        this.uuid = playerUUID;

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
