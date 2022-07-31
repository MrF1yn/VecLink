package dev.MrFlyn.FalconClient;

import com.google.gson.JsonObject;
import dev.MrFlyn.FalconClient.ClientHandlers.PacketHandler;
import dev.mrflyn.falconcommon.PacketType;
import io.netty.channel.ChannelHandlerContext;

public class PacketHandlerStandalone implements PacketHandler {
    @Override
    public void clearCaches(){
//        ConnectedFalconClient.clients.clear();
//        ConnectedFalconClient.CFC.clear();
//        ConnectedFalconClient.groups.clear();
    }
    @Override
    public void handlePayload(Object[] packet, ChannelHandlerContext ctx) {
        PacketType packetType = (PacketType)packet[0];
        switch (packetType){
            case S2C_REMOTE_CMD:
                String executor = (String) packet[1];
                String remoteCmd = (String) packet[2];
                Main.gi.log("Received Remote Command Execution request from FalconCloud Server.");
                Main.gi.log("Command: " + remoteCmd);
                Main.gi.log("Executor: " + (executor.equals("@c") ? "console" : executor));
                break;
        }
    }

    public void test(String test){
        System.out.println("FALCON TEST");
    }
}
