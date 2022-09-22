package dev.mrflyn.veclinkspigot.commands;

import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkspigot.PacketFormatterSpigot;
import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import dev.mrflyn.veclinkspigot.commands.handler.SubCommand;
import dev.mrflyn.veclinkspigot.commands.handler.VecLinkCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FindCommand implements SubCommand {

    public FindCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(Main.client.channel==null||!(Main.client.channel.isActive())){

            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString()));
            return true;
        }
        //veclink find [playerName]
        if(args.length<1){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_FIND_CMD.toString()));
            return true;
        }
        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);
        if(target!=null){
            p.teleport(target);
            return true;
        }
        List<ConnectedVecLinkClient> connectedTargetServers = ConnectedVecLinkClient.getOnlinePlayerServers0(playerName);
        if (connectedTargetServers.isEmpty()){
            p.sendMessage("Could not find the target server.");
            return true;
        }
        ConnectedVecLinkClient targetProxy = null;
        ConnectedVecLinkClient targetServer = null;
        for(ConnectedVecLinkClient c : connectedTargetServers){
            if (c.getType().equals("SPIGOT")){
                targetServer = c;
            }
            if(!(c.getType().equals("BUNGEE")&&c.getType().equals("VELOCITY")))continue;
            if (c.playersByName.containsKey(playerName)&&c.playersByName.containsKey(p.getName()))targetProxy = c;
        }
        if (targetProxy==null||targetServer==null){
            p.sendMessage("You and the target player are not connected to the same proxy! Unable to send.");
            return true;
        }
        String targetServerName = targetServer.getName();
        Bukkit.getScheduler().runTaskAsynchronously(VecLinkMainSpigot.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatFindPlayerPacket(playerName, p.getName(), targetServerName));
            Bukkit.getScheduler().runTaskLater(VecLinkMainSpigot.plugin, ()->{
                VecLinkMainSpigot.plugin.sendConnectPluginMessage(p, targetServerName);
            }, 10L);
        });
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        return null;
    }

    @Override
    public String getName() {
        return "find";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.find";
    }
}
