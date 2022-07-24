package dev.MrFlyn.FalconServer.ServerHandlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.MrFlyn.FalconServer.Main;
import dev.mrflyn.falconcommon.PacketType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PacketFormatter {

    public static JsonObject formatRemoteCmdExec(String executor, String command){
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.S2C_REMOTE_CMD.name());
        json.addProperty("executor", executor);
        json.addProperty("command", command);
        return json;
    }

    public static JsonObject authStatus(boolean status, List<String> groups) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.S2C_AUTH.name());
        json.addProperty("status", status);
        json.addProperty("groups", groups.toString());
        return json;
    }

    public static JsonObject formatClientInfoPacket(String clientName,ServerType clientType, String action) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.S2C_CLIENT_INFO.name());
        json.addProperty("action", action);
        json.addProperty("name", clientName);
        json.addProperty("client-type", clientType.name());
        return json;
    }

    public static JsonObject formatGroupInfoPacket(Collection<String> groups) {
        JsonObject json = new JsonObject();
        List<String> groupList = new ArrayList<>(groups);
        json.addProperty("type", PacketType.S2C_GROUP_INFO.name());
        json.addProperty("group-list", groupList.toString());
        return json;
    }

    public static JsonObject formatPlayerInfoForward(String name, String uuid, String client, String action) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.S2C_PLAYER_INFO.name());
        json.addProperty("client-id", client);
        json.addProperty("action", action);
        json.addProperty("name", name);
        json.addProperty("uuid", uuid);
        return json;
    }

    public static JsonObject formatClientInfoForwardPacket(FalconClient client, String type){
        JsonObject json = new JsonObject();
        json.addProperty("type",PacketType.S2C_CLIENT_INFO_FORWARD.name());
        json.addProperty("sub-type", type);
        json.addProperty("client-type", client.getType().name());
        json.addProperty("name", client.getName());
        json.addProperty("groups", client.getGroups().toString());
        if(type.equals("BASIC")){
            json.addProperty("player-count", client.getOnlinePlayerCount());
            json.addProperty("can-join", client.isCanJoin());
        }else if(type.equals("ADVANCED")){
//            private long lastKeepAlive;
//            private long runningThreads;
//            private long cpuCores;
//            private long cpuUsagePercent;
//            private long memoryUsagePercent;
//            private long CurrentMemoryUsage;
//            private long maxMemory;
//            private long allocatedMemory;
//            private double tps1min;
//            private double tps5min;
//            private double tps15min;
//            private double mspt;
//            private String osName;
            json.addProperty("tps", Arrays.asList(client.getTps1min(),client.getTps5min(),client.getTps15min()).toString());
            json.addProperty("can-join", client.isCanJoin());
            json.addProperty("memory-info", Arrays.asList(client.getLastKeepAliveInSecs(),client.getRunningThreads(),client.getCpuCores(),client.getCpuUsagePercent(),
                    client.getMemoryUsagePercent(),client.getCurrentMemoryUsage(),client.getMaxMemory(),client.getAllocatedMemory()).toString());
            json.addProperty("mspt", client.getMspt());
            json.addProperty("os-name", client.getOsName());
            if(client.getType()==ServerType.VELOCITY||client.getType()==ServerType.BUNGEE) {
                json.addProperty("backend-servers", client.getBackendServers().toString());
            }

        }

        return json;
    }

    public static JsonObject formatChatGroupInstantiatePacket(String groupName, String chatFormat, String action){
        JsonObject json = new JsonObject();
        json.addProperty("type",PacketType.S2C_CHAT_GROUP_INIT.name());
        json.addProperty("action", action);
        json.addProperty("group-name", groupName);
        json.addProperty("chat-format", chatFormat);
        return json;
    }

    public static JsonObject formatChatDisplay(String chat, String from) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.S2C_CHAT.name());
        json.addProperty("from", from);
        json.addProperty("message", chat);
        return json;
    }

    public static JsonObject formatChatGrpDisplay(String chat, String from, String grpName, String sender) {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.S2C_CHAT_GRP.name());
        json.addProperty("from", from);
        json.addProperty("sender", sender);
        json.addProperty("group", grpName);
        json.addProperty("message", chat);
        return json;
    }
}
