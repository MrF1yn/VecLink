package FalconClientVelocity;

import com.google.gson.JsonObject;
import dev.mrflyn.falconcommon.PacketType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PacketFormatterVelocity {

    public static JsonObject formatRemoteCommandPacket(String targetServer, String executor, String command){

        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_REMOTE_CMD.name());
        json.addProperty("target", targetServer);
        json.addProperty("executor", executor);
        json.addProperty("command", command.trim());
        return json;
    }

    public static JsonObject formatPlayerInfoPacket(UUID uuid, String name, String action, int onlinePlayerCount, boolean canJoin) {

        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_PLAYER_INFO.name());
        json.addProperty("action", action);
        json.addProperty("uuid", uuid.toString());
        json.addProperty("player-name", name);
        json.addProperty("player-count", onlinePlayerCount);
        json.addProperty("can-join", canJoin);
        return json;
    }

    public static JsonObject formatKeepAlivePacket(boolean isJoinable, long[] memory, String osName, List<String> backendServers) {

        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_KEEP_ALIVE.name());
        json.addProperty("joinable-status", isJoinable);
        json.addProperty("memory-info", Arrays.toString(memory));
        json.addProperty("os-name", osName);
        json.addProperty("backend-servers", backendServers.toString());
        return json;
    }

    public static JsonObject chatSyncInstantiate() {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_CHAT_SYNC_INIT.name());
        json.addProperty("target-servers", FalconMainVelocity.plugin.config.getStringList("chat-module.target-servers").toString());
        return json;
    }

    public static JsonObject chatPacket(String msg) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_CHAT.name());
        json.addProperty("message", msg);
        return json;
    }

    public static JsonObject chatGroupMessage(String msg, String sender, String grpName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_CHAT_GRP_MSG.name());
        json.addProperty("from", sender);
        json.addProperty("group", grpName);
        json.addProperty("message", msg);
        return json;
    }

    public static JsonObject partyInvite(String ownerName, String invitedName) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.PC2S_PARTY_INVITE.name());
        json.addProperty("owner", ownerName);
        json.addProperty("invited", invitedName);
        return json;
    }




}
