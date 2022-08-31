package dev.mrflyn.veclinkspigot;

import dev.mrflyn.veclinkcommon.PacketType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PacketFormatterSpigot {

    public static Object[] formatRemoteCommandPacket(String targetServer, String executor, String command){

        List<Object> packet = Arrays.asList(
                PacketType.C2S_REMOTE_CMD.ordinal(),
                targetServer,
                executor,
                command.trim());
        return packet.toArray();
    }

    public static Object[] formatPlayerInfoPacket(UUID uuid, String name, String action, int onlinePlayerCount, boolean canJoin) {

        List<Object> packet = Arrays.asList(
                PacketType.C2S_PLAYER_INFO.ordinal(),
                action,
                uuid.toString(),
                name,
                onlinePlayerCount,
                canJoin);
        return packet.toArray();
    }

    public static Object[] formatKeepAlivePacket(double[] tps, boolean isJoinable, long[] memory, double mspt, String osName) {

        List<Object> packet = Arrays.asList(
                PacketType.C2S_KEEP_ALIVE.ordinal(),
                isJoinable,
                memory,
                osName,
                tps,
                mspt);
        return packet.toArray();
    }

    public static Object[] chatSyncInstantiate() {
        List<Object> packet = Arrays.asList(
                PacketType.C2S_CHAT_SYNC_INIT.ordinal(),
                VecLinkMainSpigot.plugin.config.getStringList("chat-module.target-servers"));
        return packet.toArray();
    }

    public static Object[] chatPacket(String msg) {
        List<Object> packet = Arrays.asList(
                PacketType.C2S_CHAT.ordinal(),
                msg);
        return packet.toArray();
    }

    public static Object[] chatGroupMessage(String msg, String sender, String grpName) {
        List<Object> packet = Arrays.asList(
                PacketType.C2S_CHAT_GRP_MSG.ordinal(),
                sender,
                grpName,
                msg);
        return packet.toArray();
    }

    public static Object[] dcVerifyInit(String playerName, UUID playerUUID) {
        List<Object> packet = Arrays.asList(
                PacketType.C2S_DC_VERIFY_INIT.ordinal(),
                playerName,
                playerUUID.toString());
        return packet.toArray();
    }


}
