package FalconClientSpigot;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PacketFormatterSpigot {

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

    public static JsonObject formatKeepAlivePacket(double[] tps, boolean isJoinable, long[] memory, double mspt, String osName) {

        JsonObject json = new JsonObject();
        json.addProperty("type", "KEEP-ALIVE");
        json.addProperty("tps", Arrays.toString(tps));
        json.addProperty("joinable-status", isJoinable);
        json.addProperty("memory-info", Arrays.toString(memory));
        json.addProperty("mspt", mspt);
        json.addProperty("os-name", osName);
        return json;
    }

    public static JsonObject chatSyncInstantiate() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CHAT-SYNC-INSTANTIATE");
        json.addProperty("target-servers", FalconMainSpigot.plugin.config.getStringList("chat-module.target-servers").toString());
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


}
