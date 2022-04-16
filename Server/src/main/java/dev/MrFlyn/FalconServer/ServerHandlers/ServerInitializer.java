package dev.MrFlyn.FalconServer.ServerHandlers;

import dev.MrFlyn.FalconServer.Main;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Main.crashDetected();
        ctx.close();
    }

    @Override
    protected void initChannel(SocketChannel arg0)
    {
        ChannelPipeline pipeline = arg0.pipeline();
        String ip = pipeline.channel().remoteAddress().toString().substring(1).split(":")[0];
        Main.debug("Incoming connection from ip: "+ip+".", false);
        if(ServerHandler.AuthorisedClients.size()>=Main.config.getMainConfig().getInt("maximum-allowed-connections")){
            Main.debug("Maximum connection limit reached. Rejecting connection from ip: "+ip+".", false);
            pipeline.close();
            return;
        }
        if(Main.config.getMainConfig().getBoolean("ip-whitelist.enabled")){
            if(!Main.config.getMainConfig().getStringList("ip-whitelist.whitelisted-ips").contains(ip)){
                Main.debug("Ip: "+ip+" is not whitelisted. Rejecting connection...", false);
                pipeline.close();
                return;
            }
        }
        Main.log("Incoming connection from allowed ip: "+ip+".", false);

//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(20000, Delimiters.lineDelimiter()));
            pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
            pipeline.addLast("encoder", new ObjectEncoder());
            pipeline.addLast("handler", new ServerHandler());




    }
}
