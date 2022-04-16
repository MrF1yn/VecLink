package dev.MrFlyn.FalconServer;

import dev.MrFlyn.FalconServer.ServerHandlers.FalconServer;
import dev.MrFlyn.FalconServer.ServerHandlers.ServerHandler;
import dev.MrFlyn.FalconServer.Utils.ConsoleSpamHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.simpleyaml.configuration.file.YamlFile;

public class Main {

    public static MainConfig config;
    public static FalconServer server;
    public static Commands commands;

    public static void main(String[] args){
        config = new MainConfig(new YamlFile("falconServer.yml"));
        config.init();
        if(config.getMainConfig().getBoolean("console-spam-detection.prevent-console-spam"))
            new ConsoleSpamHandler(config.getMainConfig().getInt("console-spam-detection.max-violations"),
                    config.getMainConfig().getLong("console-spam-detection.reset-violations-after"));
        server = new FalconServer(config.getMainConfig().getInt("port"));

        server.start();
        commands = new Commands();
        commands.start();
        for(String s : Main.config.getMainConfig().getConfigurationSection("groups").getKeys(false)){
            ServerHandler.ChannelsByGroups.put(s, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
    }

    public static <T> void debug(T message, boolean bypassSpam){
        if(config.getMainConfig().getBoolean("debug")) {
            if(Main.config.getMainConfig().getBoolean("console-spam-detection.prevent-console-spam")&&!bypassSpam)
                ConsoleSpamHandler.consoleSpamHandler.onMessageSend();
            if(!ConsoleSpamHandler.isSpammingIfEnabled()||bypassSpam)
                System.out.println("[FalconCloud][Debug] " + message);
        }
    }


    public static void crashDetected(){
        log("Thread-Limit reached. Restarting the Instance...", true);
        System.exit(0);
    }

    public static <T> void log(T message, boolean bypassSpam) {
        if(Main.config.getMainConfig().getBoolean("console-spam-detection.prevent-console-spam")&&!bypassSpam)
            ConsoleSpamHandler.consoleSpamHandler.onMessageSend();
        if(!ConsoleSpamHandler.isSpammingIfEnabled()||bypassSpam)
            System.out.println("[FalconCloud][Log] " + message);

    }
}
