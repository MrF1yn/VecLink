package dev.mrflyn.veclinkcommon;

import java.util.List;

public enum PacketType {
//C2S means the packet is sent from VecLinkClient to FalconServer.
//S2C means the packet is sent from FalconServer to VecLinkClient.
//PC2S means the packet is only sent from a proxyVecLinkClient like Velocity or Bungee.

    C2S_AUTH(new PacketFormat(Integer.class,String.class,String.class,String.class)
            ,"The first packet sent from client to server containing the auth details."),
    C2S_REMOTE_CMD(new PacketFormat(Integer.class,String.class,String.class,String.class)
            ,"Packet for remote server command to execute in separate server."),
    C2S_PLAYER_INFO(new PacketFormat(Integer.class,String.class,String.class,String.class,Integer.class,Boolean.class)
            ,"This packet is sent on player join and leave containing player details and total count."),

    C2S_KEEP_ALIVE(new PacketFormat(Integer.class,Boolean.class,List.class,String.class,List.class,Double.class)
            ,"This packet is sent after every 15secs containing basic server info and status."),

    C2S_CHAT_SYNC_INIT(new PacketFormat(Integer.class,List.class)
            ,"This packet is sent after successful auth with the server and begins the process of chat syncing with the target server in the config."),
    C2S_CHAT(new PacketFormat(Integer.class,String.class)
            ,"This packet contains chat message for syncing."),
    C2S_CHAT_GRP_MSG(new PacketFormat(Integer.class,String.class,String.class,String.class)
            ,"This packet is similar to C2S_CHAT packet but only for particular chat groups."),
    C2S_DC_VERIFY_INIT(new PacketFormat(),
            "This packet is sent when a player executes /veclink dcverify command ingame. It initiates the minecraft-discord linking process."),
    C2S_DC_VERIFY_REQ(new PacketFormat(),
            "This packet is sent from veclink srv when the user inputs the received token for the final verification."),


    PC2S_PARTY_INVITE("Only for proxy clients, This packet is sent when someone invites someone to a party."),

    //Server->Client
    S2C_AUTH(new PacketFormat(Integer.class,Boolean.class,List.class)
            ,"This packet contains the auth status of the client."),
    S2C_REMOTE_CMD(new PacketFormat(Integer.class,String.class,String.class)
            ,"This packet is forwarded from the VecLinkServer to the target VecLinkClient."),
    S2C_CLIENT_INFO(new PacketFormat(Integer.class,String.class,String.class,String.class)
            ,"This packet is forwarded to all clients when a client connects to the veclinkServer."),
    S2C_GROUP_INFO(new PacketFormat(Integer.class,List.class)
            ,"This packet is forwarded to a client when it connects to the veclink server. It contains the channelGroup Names."),
    S2C_PLAYER_INFO(new PacketFormat(Integer.class,String.class,String.class,String.class,String.class)
            ,"This packet is forwarded to a client when it connects to the veclink server and also when the Server receives a C2S_PLAYER_INFO packet. " +
            "It contains player info."),

    S2C_CLIENT_INFO_FORWARD("This packet is forwarded to all connected clients when any of the clients send a keep alive packet. It contains essential client info."),

    S2C_CHAT_GROUP_INIT(new PacketFormat(Integer.class,String.class,String.class,String.class)
            ,"This packet is forwarded when a client connects to the server and contains all defined chatGroups."),
    S2C_CHAT(new PacketFormat(Integer.class,String.class,String.class)
            ,"This packet is sent to all clients when the server receives a chat sync packet from a client."),
    S2C_CHAT_GRP(new PacketFormat(Integer.class,String.class,String.class,String.class,String.class)
            ,"This packet is sent to all clients when the server receives a chat group sync packet from a client."),
    S2C_DC_VERIFY_INIT(new PacketFormat(),
            "This packet is sent to the client and contains the verification token."),
    S2C_DC_VERIFY_ACK(new PacketFormat(),
            "This packet is sent to the client and veclink srv after the token verification is complete and contains the status of the verification procedure."),

    S2C_PARTY_INVITE("This packet is sent to the client which contains player who is invited to a party.")


    ;

    private final String description;
    private final PacketFormat format;

    PacketType(PacketFormat packetFormat ,String description) {
        this.description = description;
        this.format = packetFormat;
    }

    PacketType(String description) {
        this.description = description;
        this.format = null;
    }

    public String getDescription() {
        return description;
    }

    //used only for receiving packets.
    public static boolean validatePacket(Object[] packet, ClientType clientType){
        if(packet==null)return false;
        if(packet.length<1)return false;
        if(!(packet[0] instanceof Integer))return false;
        int packetID = (int) packet[0];
        if (packetID>=PacketType.values().length||packetID<0)return false;
        PacketType type = PacketType.values()[packetID];
        if(type != C2S_AUTH) {
            boolean isNotSpigot = (clientType == ClientType.BUNGEE || clientType == ClientType.VELOCITY || clientType == ClientType.DISCORD_SRV);
            //exception cases start
            if (type == PacketType.C2S_KEEP_ALIVE && isNotSpigot) {
                if (packet.length != 5) return false; //invalid size
                if (!(packet[1] instanceof Boolean)) return false;
                if (!(packet[2] instanceof List||packet[2].getClass().isArray())) return false;
                if (!(packet[3] instanceof String)) return false;
                if (!(packet[4] instanceof List||packet[4].getClass().isArray())) return false;
                return true;
            }
            if (type == PacketType.S2C_CLIENT_INFO_FORWARD) {
                //check basic or advanced
                if (!(packet.length == 7 || packet.length == 10 || packet.length == 11)) return false; //invalid size
                if (!(packet[1] instanceof String)) return false;
                if (!(packet[2] instanceof String)) return false;
                if (!(packet[3] instanceof String)) return false;
                if (!(packet[4] instanceof List||packet[4].getClass().isArray())) return false;
                //Basic packet
                if (packet.length == 7) {
                    if (!(packet[5] instanceof Integer)) return false;
                    if (!(packet[6] instanceof Boolean)) return false;
                    return true;
                }
                //Advanced packet
                //check proxy
                if (!(packet[5] instanceof List||packet[5].getClass().isArray())) return false;
                if (!(packet[6] instanceof Boolean)) return false;
                if (!(packet[7] instanceof List||packet[7].getClass().isArray())) return false;
                if (!(packet[8] instanceof Double)) return false;
                if (!(packet[9] instanceof String)) return false;
                if (packet.length == 11) {
                    if (!(packet[10] instanceof List||packet[10].getClass().isArray())) return false;
                }
                return true;

            }
            //exception cases end
        }
        if (type.format == null)return false; //packet format not defined
        if(packet.length!=type.format.getTypes().length)return false; //invalid format cause size doesnt match

        for(int i = 1; i<packet.length; i++){
            if(type.format.getTypes()[i].equals(List.class)){
                if(!(packet[i] instanceof List||packet[i].getClass().isArray())) return false;
            }
            else if (!type.format.getTypes()[i].isInstance(packet[i]))return false; //datatype doesn't match
        }

        return true;
    }

}
