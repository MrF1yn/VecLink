package dev.MrFlyn.FalconClient.ClientHandlers;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;

public interface PacketHandler {

     void handlePayload(JsonObject json, ChannelHandlerContext ctx);

     void clearCaches();

     void test(String test);


}
