package dev.mrflyn.veclinkspigot.commands;

import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkcommon.ClientType;
import dev.mrflyn.veclinkcommon.VLPlayer;
import dev.mrflyn.veclinkspigot.PacketFormatterSpigot;
import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import dev.mrflyn.veclinkspigot.commands.handler.SubCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getLCS;
import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getMiniMessage;

public class PlayerInfoCommand implements SubCommand {
    String format;
    public PlayerInfoCommand(){
        format = "<aqua>------------------</aqua>\n" +
                "<green><hover:show_text:'<red>Click to Copy'>PlayerName</hover>: <gold><insert:%playerName%>%playerName%</insert></gold></green>\n" +
                "<green><hover:show_text:'<red>Click to Copy'>PlayerUUID</hover>: <gold><insert:%playerUUID%>%playerUUID%</insert></gold></green>\n" +
                "<green><hover:show_text:'<red>Click to Copy'>DiscordID</hover>: <gold><insert:%discordID%>%discordID%</insert></gold></green>\n" +
                "<green><hover:show_text:'<red>Click to Copy'>DiscordName</hover>: <gold><insert:@%discordName%>%discordName%</insert></gold></green>\n" +
                "<aqua>------------------</aqua>";
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //vecLink playerInfo <playerName>
        if(Main.client.channel==null||!(Main.client.channel.isActive())){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString()));
            return true;
        }
        if(args.length<1){
            sender.sendMessage("Please mention the playerName.");
        }
        if(Main.db==null||!Main.db.isConnected()){
            sender.sendMessage("Database offline please try again later.");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(VecLinkMainSpigot.plugin, ()->{
            VLPlayer p =  Main.db.getPlayerInfoFromMinecraftName(args[0]);
            if(p==null){
                sender.sendMessage("Player not Found");
                return;
            }
            String msg = format
                    .replace("%playerName%", p.getName())
                    .replace("%playerUUID%", p.getUUID().toString())
                    .replace("%discordID%", p.getUserID()==null?"N/A":p.getUserID())
                    .replace("%discordName%", p.getUserName()==null?"N/A":p.getUserName());

                Audience audience = VecLinkMainSpigot.plugin.adventure().sender(sender);
                audience.sendMessage(getMiniMessage().deserialize(msg));
        });
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "playerInfo";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.playerInfo";
    }
}
