package dev.mrflyn.veclinkserver.ServerHandlers;

import dev.mrflyn.veclinkcommon.PacketType;
import dev.mrflyn.veclinkcommon.ClientType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PacketFormatter {

    public static Object[] formatRemoteCmdExec(String executor, String command){
        List<Object> packet = Arrays.asList(
                PacketType.S2C_REMOTE_CMD.ordinal(),
                executor,
                command);
        return packet.toArray();
    }

    public static Object[] authStatus(boolean status, List<String> groups) {
        List<Object> packet = Arrays.asList(
                PacketType.S2C_AUTH.ordinal(),
                status,
                groups);
        return packet.toArray();
    }

    public static Object[] formatClientInfoPacket(String clientName, ClientType clientType, String action) {
        List<Object> packet = Arrays.asList(
                PacketType.S2C_CLIENT_INFO.ordinal(),
                action,
                clientName,
                clientType.name());
        return packet.toArray();
    }

    public static Object[] formatGroupInfoPacket(Collection<String> groups) {
        List<String> groupList = new ArrayList<>(groups);
        List<Object> packet = Arrays.asList(
                PacketType.S2C_GROUP_INFO.ordinal(),
                groupList);
        return packet.toArray();
    }

    public static Object[] formatPlayerInfoForward(String name, String uuid, String client, String action) {
        List<Object> packet = Arrays.asList(
                PacketType.S2C_PLAYER_INFO.ordinal(),
                client,
                action,
                name,
                uuid);
        return packet.toArray();
    }

    public static Object[] formatClientInfoForwardPacket(VecLinkClient client, String type){
        List<Object> packet = new ArrayList<>();
        packet.add(PacketType.S2C_CLIENT_INFO_FORWARD.ordinal());
        packet.add(type);
        packet.add(client.getType().name());
        packet.add(client.getName());
        packet.add(client.getGroups());
        if(type.equals("BASIC")){
            packet.add(client.getOnlinePlayerCount());
            packet.add(client.isCanJoin());
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
            packet.add(Arrays.asList(client.getTps1min(),client.getTps5min(),client.getTps15min()));
            packet.add(client.isCanJoin());
            packet.add(Arrays.asList(client.getLastKeepAliveInSecs(),client.getRunningThreads(),client.getCpuCores(),client.getCpuUsagePercent(),
                    client.getMemoryUsagePercent(),client.getCurrentMemoryUsage(),client.getMaxMemory(),client.getAllocatedMemory()));
            packet.add(client.getMspt());
            packet.add(client.getOsName());
            if(client.getType()== ClientType.VELOCITY||client.getType()== ClientType.BUNGEE) {
                packet.add(client.getBackendServers());
            }

        }

        return packet.toArray();
    }

    public static Object[] formatChatGroupInstantiatePacket(String groupName, String chatFormat, String action){
        List<Object> packet = Arrays.asList(
                PacketType.S2C_CHAT_GROUP_INIT.ordinal(),
                action,
                groupName,
                chatFormat);
        return packet.toArray();
    }

    public static Object[] formatChatDisplay(String chat, String from) {
        List<Object> packet = Arrays.asList(
                PacketType.S2C_CHAT.ordinal(),
                from,
                chat);
        return packet.toArray();
    }

    public static Object[] formatChatGrpDisplay(String chat, String from, String grpName, String sender) {
        List<Object> packet = Arrays.asList(
                PacketType.S2C_CHAT_GRP.ordinal(),
                from,
                sender,
                grpName,
                chat);
        return packet.toArray();
    }
}
