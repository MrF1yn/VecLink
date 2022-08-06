package dev.mrflyn.veclinkdiscordsrv;

import dev.mrflyn.veclinkcommon.PacketType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PacketFormatterDiscordSRV {

    public static Object[] formatRemoteCommandPacket(String targetServer, String executor, String command) {

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

    public static Object[] formatKeepAlivePacket(boolean isJoinable, long[] memory, String osName, List<String> backendServers) {

        List<Object> packet = Arrays.asList(
                PacketType.C2S_KEEP_ALIVE.ordinal(),
                isJoinable,
                memory,
                osName,
                backendServers);
        return packet.toArray();
    }

    public static Object[] chatSyncInstantiate() {
        List<Object> packet = Arrays.asList(
                PacketType.C2S_CHAT_SYNC_INIT.ordinal(),
                VecLinkMainDiscordSRV.plugin.config.getStringList("chat-module.target-servers"));
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

    public static Object[] partyInvite(String ownerName, String invitedName) {
        List<Object> packet = Arrays.asList(
                PacketType.PC2S_PARTY_INVITE.ordinal(),
                ownerName,
                invitedName);
        return packet.toArray();
    }


}
