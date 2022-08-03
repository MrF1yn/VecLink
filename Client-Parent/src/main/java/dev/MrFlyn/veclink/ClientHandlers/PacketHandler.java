package dev.mrflyn.veclink.ClientHandlers;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;

public interface PacketHandler {

     void handlePayload(Object[] packet, ChannelHandlerContext ctx);

     void clearCaches();

     void test(String test);


}
