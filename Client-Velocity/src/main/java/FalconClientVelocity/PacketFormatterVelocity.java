package FalconClientVelocity;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PacketFormatterVelocity {

    public static JsonObject formatRemoteCommandPacket(String targetServer, String executor, String command){

        JsonObject json = new JsonObject();
        json.addProperty("type", "REMOTE-CMD");
        json.addProperty("target", targetServer);
        json.addProperty("executor", executor);
        json.addProperty("command", command.trim());
        return json;
    }

    public static JsonObject formatPlayerInfoPacket(UUID uuid, String name, String action, int onlinePlayerCount, boolean canJoin) {

        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER-INFO");
        json.addProperty("action", action);
        json.addProperty("uuid", uuid.toString());
        json.addProperty("player-name", name);
        json.addProperty("player-count", onlinePlayerCount);
        json.addProperty("can-join", canJoin);
        return json;
    }

    public static JsonObject formatKeepAlivePacket(boolean isJoinable, long[] memory, String osName, List<String> backendServers) {

        JsonObject json = new JsonObject();
        json.addProperty("type", "KEEP-ALIVE");
        json.addProperty("joinable-status", isJoinable);
        json.addProperty("memory-info", Arrays.toString(memory));
        json.addProperty("os-name", osName);
        json.addProperty("backend-servers", backendServers.toString());
        return json;
    }

    public static JsonObject chatSyncInstantiate() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CHAT-SYNC-INSTANTIATE");
        json.addProperty("target-servers", FalconMainVelocity.plugin.config.getStringList("chat-module.target-servers").toString());
        return json;
    }

    public static JsonObject chatPacket(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CHAT");
        json.addProperty("message", msg);
        return json;
    }

    public static JsonObject chatGroupMessage(String msg, String sender, String grpName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CHAT-GRP-MSG");
        json.addProperty("from", sender);
        json.addProperty("group", grpName);
        json.addProperty("message", msg);
        return json;
    }

    public static JsonObject partyInvite(String ownerName, String invitedName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PARTY-INVITE");
        json.addProperty("owner", ownerName);
        json.addProperty("invited", invitedName);
        return json;
    }




}
