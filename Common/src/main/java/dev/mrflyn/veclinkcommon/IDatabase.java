package dev.mrflyn.veclinkcommon;



import java.util.UUID;

public interface IDatabase {

    String name();

    boolean connect();

    boolean isConnected();
    void disconnect();

    void init();

    void saveUser(String userID, String userName, UUID minecraftUUID, String minecraftName);

    VLPlayer getPlayerInfoFromUserID(String userID);

    VLPlayer getPlayerInfoFromUserName(String userName);

    VLPlayer getPlayerInfoFromMinecraftName(String name);

    VLPlayer getPlayerInfoFromMinecraftUUID(UUID uuid);

    void deletePlayerInfoFromUserID(String userID);

    void deletePlayerInfoFromMinecraftUUID(UUID uuid);
}
