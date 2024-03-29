package dev.mrflyn.veclinkspigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.mrflyn.veclinkspigot.chat.ChatHandler;
import dev.mrflyn.veclinkspigot.chat.ChatListener;
import dev.mrflyn.veclinkspigot.commands.*;
import dev.mrflyn.veclinkspigot.commands.handler.VecLinkCommand;
import dev.mrflyn.veclinkspigot.support.PAPISupport;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import dev.mrflyn.veclinkspigot.utils.MemoryUtil;
import dev.mrflyn.veclinkspigot.utils.SpigotReflection;
import dev.mrflyn.veclink.GlobalInterface;
import dev.mrflyn.veclink.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.cloud.CloudExpansion;
import me.clip.placeholderapi.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class VecLinkMainSpigot extends JavaPlugin implements GlobalInterface{
    private BukkitTask keepAliveTask;
    public static VecLinkMainSpigot plugin;
    public List<String> groups;
    public FileConfiguration config;
    public ChatHandler chatHandler;
    public ConcurrentHashMap<String,String>chatGroups;
    public String dcChatMonitorFormat = "";
    public ConcurrentHashMap<Player,List<String>>playerChatGroupStatus;
    public LegacyComponentSerializer lcs;
    public MiniMessage miniMessage;

    private BukkitAudiences adventure;

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public void onEnable(){
        plugin = this;
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.adventure = BukkitAudiences.create(this);
        lcs = LegacyComponentSerializer.legacySection();
        miniMessage = MiniMessage.miniMessage();
        playerChatGroupStatus = new ConcurrentHashMap<>();
        chatGroups = new ConcurrentHashMap<>();
        Main.gi = this;
        Main.pi = new PacketHandlerSpigot();
        saveResource("spigot-config.yml",false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "spigot-config.yml"));
        Main.enable();
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        chatHandler = new ChatHandler();
        if(config.getBoolean("chat-module.enabled")){
            getServer().getPluginManager().registerEvents(new ChatListener(), this);
            if(VecLinkMainSpigot.plugin.config.getBoolean("chat-module.sync-chats")){
                chatHandler.startChatSyncTask();
            }
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hook into PlaceholderAPI support!");
            final CloudExpansion expansion = PlaceholderAPIPlugin.getInstance().getCloudExpansionManager()
                    .findCloudExpansionByName("Math").orElse(null);
            if(expansion!=null) {
                PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().downloadExpansion(expansion, expansion.getVersion()).whenComplete((file, exception) -> {
                    if (exception != null) {
                        Msg.msg(Bukkit.getConsoleSender(),
                                "&cFailed to download expansion: &f" + exception.getMessage());
                        return;
                    }

                    Msg.msg(Bukkit.getConsoleSender(),
                            "&aSuccessfully downloaded expansion &f" + expansion.getName() + " [" + expansion
                                    .getVersion() + "] &ato file: &f" + file.getName());

                    PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().clean();
                    PlaceholderAPIPlugin.getInstance().getCloudExpansionManager()
                            .fetch(PlaceholderAPIPlugin.getInstance().getPlaceholderAPIConfig().cloudAllowUnverifiedExpansions());
                    PlaceholderAPIPlugin.getInstance().reloadConf(Bukkit.getConsoleSender());
                });
            }

            new PAPISupport().register();
        }
//        getCommand("veclink").setExecutor(new FalconCMD());
//        getCommand("veclink").setTabCompleter(new TabComplete());
        VecLinkCommand veclinkCommand = new VecLinkCommand(
                new StatusCommand(),
                new RemoteCommand(),
                new ReconnectCommand(),
                new MuteChatCommand(),
                new DisconnectCommand(),
                new ChatGroupCommand(),
                new ReloadCommand(),
                new ListCommand(),
                new PlayerInfoCommand(),
                new DcVerifyCommand(),
                new FindCommand()
        );
        getCommand("veclink").setExecutor(veclinkCommand);
        getCommand("veclink").setTabCompleter(veclinkCommand);

    }

    public void onReload(){
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        chatHandler.stopChatSyncTask();
        chatHandler.pendingChats.clear();
        Main.config.init();
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "spigot-config.yml"));
        if(config.getBoolean("chat-module.enabled")){
            getServer().getPluginManager().registerEvents(new ChatListener(), this);
            chatHandler = new ChatHandler();
            if(VecLinkMainSpigot.plugin.config.getBoolean("chat-module.sync-chats")){
                chatHandler.startChatSyncTask();
            }
        }
    }

    public boolean isJoinable(){
        return !getServer().hasWhitelist();
    }

    public void onDisable(){
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        Main.gi.stopKeepAliveTask();
        chatHandler.stopChatSyncTask();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }


    @Override
    public <T> void log(T message) {
            getLogger().info(message.toString());

    }

    @Override
    public <T> void debug(T message) {

        if(Main.config.getMainConfig().getBoolean("debug"))
            getLogger().info("[Debug] "+message.toString());
    }


    @Override
    public String getServerType() {
        return "SPIGOT";
    }

    @Override
    public String getConfigLocation() {
        return "plugins/VecLinkClient/";
    }

    @Override
    public void startKeepAliveTask() {
        if(keepAliveTask!=null)return;

        keepAliveTask =
                Bukkit.getScheduler().runTaskTimerAsynchronously(VecLinkMainSpigot.plugin, ()->{
                    Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatKeepAlivePacket(SpigotReflection.spigotReflection().recentTps(),isJoinable(),
                            MemoryUtil.getFormattedMemory(), SpigotReflection.spigotReflection().averageTickTime(),MemoryUtil.getOsName()));
                    Main.gi.debug("Sent keep-alive to VecLinkServer.");
                },0L, 300L);

    }

    @Override
    public void stopKeepAliveTask() {
        if(keepAliveTask==null)return;
        keepAliveTask.cancel();
        keepAliveTask = null;
    }

    public static String PAPIparseIfAvailable(Player p, String s){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(p, s);
        }
        return s.replace("%", "");
    }

    public static MiniMessage getMiniMessage(){
        return VecLinkMainSpigot.plugin.miniMessage;
    }

    public static LegacyComponentSerializer getLCS() {
        return VecLinkMainSpigot.plugin.lcs;
    }

    public void sendConnectPluginMessage(Player p, String server){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
