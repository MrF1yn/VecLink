package dev.mrflyn.falconcommon;

public enum PacketType {
//C2S means the packet is sent from FalconClient to FalconServer.
//S2C means the packet is sent from FalconServer to FalconClient.
//PC2S means the packet is only sent from a proxyFalconClient like Velocity or Bungee.

    C2S_AUTH("The first packet sent from client to server containing the auth details."),
    C2S_REMOTE_CMD("Packet for remote server command to execute in separate server."),
    C2S_PLAYER_INFO("This packet is sent on player join and leave containing player details and total count."),
    C2S_KEEP_ALIVE("This packet is sent after every 15secs containing basic server info and status."),
    C2S_CHAT_SYNC_INIT("This packet is sent after successful auth with the server and begins the process of chat syncing with the target server in the config."),
    C2S_CHAT("This packet contains chat message for syncing."),
    C2S_CHAT_GRP_MSG("This packet is similar to C2S_CHAT packet but only for particular chat groups."),
    PC2S_PARTY_INVITE("Only for proxy clients, This packet is sent when someone invites someone to a party."),

    ;

    private final String description;

    PacketType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
