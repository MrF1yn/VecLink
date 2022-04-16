package dev.MrFlyn.FalconClient.ClientHandlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private FalconClient client;
    public ClientInitializer(FalconClient client){
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel arg0)
    {
        ChannelPipeline pipeline = arg0.pipeline();

//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(20000, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        pipeline.addLast("encoder", new ObjectEncoder());
        pipeline.addLast("handler", new ClientHandler(client));



    }
}
