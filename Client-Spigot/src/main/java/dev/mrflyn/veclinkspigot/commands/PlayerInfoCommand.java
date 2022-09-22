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
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ListIterator;

import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getLCS;
import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getMiniMessage;

public class PlayerInfoCommand implements SubCommand {
    String format;
    public PlayerInfoCommand(){
        format = "<aqua>------------------</aqua>\n" +
                "<green><hover:show_text:'<red>Shift-Click to Copy'><insert:%playerName%>PlayerName: <gold>%playerName%</insert></hover></gold></green>\n" +
                "<green><hover:show_text:'<red>Shift-Click to Copy'><insert:%playerUUID%>PlayerUUID: <gold>%playerUUID%</insert></hover></gold></green>\n" +
                "<green><hover:show_text:'<red>Shift-Click to Copy'><insert:%discordID%>DiscordID: <gold>%discordID%</insert></hover></gold></green>\n" +
                "<green><hover:show_text:'<red>Shift-Click to Copy'><insert:@%discordName%>DiscordName: <gold>%discordName%</insert></hover></gold></green>\n" +
                "<green>Status: %status%</green>\n" +
                "<green>ConnectedServers: %connectedServers%</green>\n" +
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
            String status = "<red>OFFLINE</red>";
            String servers = "";
            if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))){
                status = "<green><hover:show_text:'<red>Click to Teleport'><click:run_command:/veclink:find "+args[0]+">ONLINE</click></hover></green>";
            }else {
                List<String> connectedServers = ConnectedVecLinkClient.getOnlinePlayerServers(args[0]);
                if (!connectedServers.isEmpty()) {
                    status = "<green><hover:show_text:'<red>Click to Teleport'><click:run_command:/veclink:find "+args[0]+">ONLINE</click></hover></green>";
                    servers = StringUtils.join(connectedServers, " ,");
                }
            }

            String msg = "";
            if(p==null){
                msg = format
                        .replace("%playerName%", args[0])
                        .replace("%playerUUID%", "N/A")
                        .replace("%discordID%", "N/A")
                        .replace("%discordName%", "N/A")
                        .replace("%status%", status)
                        .replace("%connectedServers%", "<gold>"+servers+"</gold>")
                        ;
            }
            else {
                msg = format
                        .replace("%playerName%", p.getName())
                        .replace("%playerUUID%", p.getUUID().toString())
                        .replace("%discordID%", p.getUserID() == null ? "N/A" : p.getUserID())
                        .replace("%discordName%", p.getUserName() == null ? "N/A" : p.getUserName())
                        .replace("%status%", status)
                        .replace("%connectedServers%", "<gold>" + servers + "</gold>")
                ;
            }


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
