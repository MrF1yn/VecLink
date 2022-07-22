package FalconClientSpigot;

import com.google.gson.JsonObject;
import dev.mrflyn.falconcommon.PacketType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PacketFormatterSpigot {

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

    public static JsonObject formatKeepAlivePacket(double[] tps, boolean isJoinable, long[] memory, double mspt, String osName) {

        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_KEEP_ALIVE.name());
        json.addProperty("tps", Arrays.toString(tps));
        json.addProperty("joinable-status", isJoinable);
        json.addProperty("memory-info", Arrays.toString(memory));
        json.addProperty("mspt", mspt);
        json.addProperty("os-name", osName);
        return json;
    }

    public static JsonObject chatSyncInstantiate() {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.C2S_CHAT_SYNC_INIT.name());
        json.addProperty("target-servers", FalconMainSpigot.plugin.config.getStringList("chat-module.target-servers").toString());
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


}
