package dev.MrFlyn.FalconClient;

import com.google.gson.JsonObject;
import dev.MrFlyn.FalconClient.ClientHandlers.PacketHandler;
import io.netty.channel.ChannelHandlerContext;

public class PacketHandlerStandalone implements PacketHandler {
    @Override
    public void clearCaches(){
//        ConnectedFalconClient.clients.clear();
//        ConnectedFalconClient.CFC.clear();
//        ConnectedFalconClient.groups.clear();
    }
    @Override
    public void handlePayload(JsonObject json, ChannelHandlerContext ctx) {
        switch (json.get("type").getAsString()) {
            case "REMOTE-CMD-EXECUTE":
                String remoteCmd = json.get("command").getAsString();
                String executor = json.get("executor").getAsString();
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
