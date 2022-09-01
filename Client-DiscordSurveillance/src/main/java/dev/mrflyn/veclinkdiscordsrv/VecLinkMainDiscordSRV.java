package dev.mrflyn.veclinkdiscordsrv;

import dev.mrflyn.veclink.GlobalInterface;

import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkdiscordsrv.commands.AddRole;
import dev.mrflyn.veclinkdiscordsrv.commands.handler.VecLinkCommand;
import dev.mrflyn.veclinkdiscordsrv.utils.MemoryUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.management.MemoryManagerMXBean;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class VecLinkMainDiscordSRV implements GlobalInterface {
    public List<String> groups;
    public FileConfiguration config;
    private TimerTask keepAliveTask;
    public static String configLocation = "configs/";
    public List<String> backendServers;
    Timer timer = new Timer("0");
    public static VecLinkMainDiscordSRV plugin;
    public static JDA jda;
    public VecLinkCommand cmdHandler;

    public static void main(String[] args){
        VecLinkMainDiscordSRV main = new VecLinkMainDiscordSRV();
        main.enable();
    }

    public void enable(){
        plugin = this;
        backendServers = new ArrayList<>();
        Main.gi = this;
        Main.pi = new PacketHandlerDiscordSRV();
        saveResource("config.yml",false);
        saveResource("status_embed.json", false);
        config = YamlConfiguration.loadConfiguration(new File(getConfigLocation(), "config.yml"));
        String token = config.getString("bot_token");
        if (token==null || token.isEmpty()){
            Main.gi.log("Invalid Bot Token.");
            System.exit(0);
        }
        cmdHandler = new VecLinkCommand(new AddRole());
        Main.enable();
        new Thread(() -> {
            try {
                Main.gi.log("Starting JDA instance!");
                jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                        .addEventListeners(new Listeners())
                        .setActivity(Activity.playing("VecLink"))
                        .setChunkingFilter(ChunkingFilter.ALL)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .build();
                jda.awaitReady();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Main.gi.log("Registering slash-commands!");
            jda.upsertCommand("status","Get the status of a server.")
                    .addOptions(
                            new OptionData(OptionType.STRING,
                                    "server-name",
                                    "Optional Name of the server.",
                                    false)
                    ).queue();
        }).start();

    }


    public boolean isJoinable(){
        return false;
    }

    @Override
    public <T> void log(T message) {
        System.out.println("[VecLinkClient] "+message.toString());
    }

    @Override
    public <T> void debug(T message) {

        if (Main.config.getMainConfig().getBoolean("debug"))
            System.out.println("[VecLinkClient][Debug] "+message.toString());

    }


    @Override
    public String getServerType() {
        return "DISCORD_SRV";
    }

    @Override
    public String getConfigLocation() {
        return configLocation;
    }

    @Override
    public void startKeepAliveTask() {
            if (keepAliveTask != null) return;
            backendServers = Arrays.asList("TEST");
            keepAliveTask = new TimerTask() {
                @Override
                public void run() {
                    Main.client.channel.writeAndFlush(PacketFormatterDiscordSRV.formatKeepAlivePacket(isJoinable(),
                            MemoryUtil.getFormattedMemory(), MemoryUtil.getOsName(), backendServers));
                    Main.gi.debug("Sent keep-alive to VecLinkServer.");
                }
            };
            Main.gi.debug("Started keep-alive task.");
            timer.scheduleAtFixedRate(keepAliveTask,0L, 15000L);


    }

    @Override
    public void stopKeepAliveTask() {
            if (keepAliveTask == null) return;
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
                File outFile = new File(configLocation, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(configLocation, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        Main.gi.log("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
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
                    Main.gi.log("Could not save " + outFile.getName() + " to " + outFile);
                    var10.printStackTrace();
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

}
