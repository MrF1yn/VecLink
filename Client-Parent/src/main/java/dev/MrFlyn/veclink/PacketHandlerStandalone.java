package dev.MrFlyn.veclink;

import dev.MrFlyn.veclink.ClientHandlers.PacketHandler;
import dev.mrflyn.veclinkcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;

public class PacketHandlerStandalone implements PacketHandler {
    @Override
    public void clearCaches(){
//        ConnectedVecLinkClient.clients.clear();
//        ConnectedVecLinkClient.CFC.clear();
//        ConnectedVecLinkClient.groups.clear();
    }
    @Override
    public void handlePayload(Object[] packet, ChannelHandlerContext ctx) {
        PacketType packetType = (PacketType)packet[0];
        switch (packetType){
            case S2C_REMOTE_CMD:
                String executor = (String) packet[1];
                String remoteCmd = (String) packet[2];
                Main.gi.log("Received Remote Command Execution request from VecLink Server.");
                Main.gi.log("Command: " + remoteCmd);
                Main.gi.log("Executor: " + (executor.equals("@c") ? "console" : executor));
                break;
        }
    }

    public void test(String test){
        System.out.println("FALCON TEST");
    }
}
