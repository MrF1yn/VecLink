package dev.mrflyn.veclinkvelocity;
import dev.mrflyn.veclinkvelocity.commands.*;
import dev.mrflyn.veclinkvelocity.commands.handler.VecLinkCommand;
import dev.mrflyn.veclinkvelocity.utils.MemoryUtil;
import dev.mrflyn.veclinkvelocity.API.placeholderapinative.MathExpansion;
import dev.mrflyn.veclinkvelocity.API.placeholderapinative.PlaceholderAPI;
import dev.mrflyn.veclinkvelocity.utils.PAPISupport;
import dev.mrflyn.veclinkvelocity.utils.PingHandler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.mrflyn.veclink.GlobalInterface;
import dev.mrflyn.veclink.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Plugin(id = "veclinkclient", name = "VecLinkClient", version = "0.0",
        url = "https://vectlabs.xyz", description = "VecLink client for velocity.", authors = {"MrFlyn"})
public class VecLinkMainVelocity implements GlobalInterface{
    public PlaceholderAPI papi;
    public final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private ScheduledTask keepAliveTask;
    public static VecLinkMainVelocity plugin;
    public List<String> groups;
    public FileConfiguration config;
    public MiniMessage miniMessage;
    public LegacyComponentSerializer lcs;
    public PingHandler pingHandler;
    @Inject
    public VecLinkMainVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        logger.info("VecLinkClient-Velocity.");
    }

    @Subscribe
    public void onShutDown(ProxyShutdownEvent e){
        Main.gi.stopKeepAliveTask();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Main.gi = this;
        Main.pi = new PacketHandlerVelocity();
        saveResource("velocity-config.yml",false);
        config = YamlConfiguration.loadConfiguration(new File(dataDirectory.toFile(), "velocity-config.yml"));
        miniMessage = MiniMessage.miniMessage();
         lcs = LegacyComponentSerializer.legacySection();
        plugin = this;
        papi = PlaceholderAPI.createPAPI();
        if(papi!=null) {
            papi.registerPlaceholder(new MathExpansion());
            papi.registerPlaceholder(new PAPISupport());
        }
        pingHandler = new PingHandler();
        Main.enable();
        VecLinkCommand command = new VecLinkCommand(
                new StatusCommand(),
                new RemoteCommand(),
                new ReconnectCommand(),
                new DisconnectCommand(),
                new ReloadCommand(),
                new ListCommand()
        );
        server.getCommandManager().register("veclinkv", command);
        server.getEventManager().register(this, new Listeners());

    }

    public void onReload(){
        Main.config.init();
        config = YamlConfiguration.loadConfiguration(new File(dataDirectory.toFile(), "velocity-config.yml"));
    }

    public boolean isJoinable(){
        return true;
    }



    @Override
    public <T> void log(T message) {
            logger.info(message.toString());

    }

    @Override
    public <T> void debug(T message) {

        if(Main.config.getMainConfig().getBoolean("debug"))
            logger.info("[Debug] "+message.toString());
    }


    @Override
    public String getServerType() {
        return "VELOCITY";
    }

    @Override
    public String getConfigLocation() {
        return "plugins/veclinkclient/";
    }

    @Override
    public void startKeepAliveTask() {
        if(keepAliveTask!=null)return;

        keepAliveTask =
                server.getScheduler().buildTask(VecLinkMainVelocity.plugin, ()->{
                    List<String> backendServers = new ArrayList<>();
                    for(RegisteredServer server: server.getAllServers()){
                        backendServers.add(server.getServerInfo().getName());
                    }
                    Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatKeepAlivePacket(isJoinable(),
                            MemoryUtil.getFormattedMemory(), MemoryUtil.getOsName(), backendServers));
                    Main.gi.debug("Sent keep-alive to VecLinkServer.");
                }).repeat(15L, TimeUnit.SECONDS).schedule();

    }

    @Override
    public void stopKeepAliveTask() {
        if(keepAliveTask==null)return;
        keepAliveTask.cancel();
        keepAliveTask = null;
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        } else {
            try {
                URL url = this.getClass().getClassLoader().getResource(filename);
                if (url == null) {
                    return null;
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } catch (IOException var4) {
                return null;
            }
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in ");
            } else {
                File outFile = new File(dataDirectory.toFile(), resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(dataDirectory.toFile(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        this.logger.info("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    this.logger.info("Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    public static MiniMessage getMiniMessage(){
        return VecLinkMainVelocity.plugin.miniMessage;
    }

    public static LegacyComponentSerializer getLCS() {
        return VecLinkMainVelocity.plugin.lcs;
    }

    public static PingHandler getPingHandler() {
        return VecLinkMainVelocity.plugin.pingHandler;
    }

}
