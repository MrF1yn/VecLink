package veclinkspigot.commands;


import veclinkspigot.commands.handler.VecLinkCommand;
import veclinkspigot.commands.handler.SubCommand;
import dev.MrFlyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.MrFlyn.veclink.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements SubCommand {
    public ListCommand(){
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length==1){
            String name = args[0];
            if(ConnectedVecLinkClient.CFC.containsKey(name)){
                ConnectedVecLinkClient c = ConnectedVecLinkClient.CFC.get(name);
                StringBuilder result = new StringBuilder("&a[" + c.getName() + "] &6(&f" + c.getOnlinePlayerCount() + "&6):&f ");
                for(String s : c.playersByName.keySet()){
                    result.append(s).append(",");
                }
                result.deleteCharAt(result.length()-1);
                if(!c.playersByName.isEmpty())
                    result.append(".");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',result.toString()));
            }
            else if(name.equals(Main.config.getMainConfig().getString("client-id"))){
                StringBuilder result = new StringBuilder("&a[" + name + "] &6(&f" + Bukkit.getOnlinePlayers().size() + "&6):&f ");
                for(Player p : Bukkit.getOnlinePlayers()){
                    result.append(p.getName()).append(",");
                }
                result.deleteCharAt(result.length()-1);
                if(!Bukkit.getOnlinePlayers().isEmpty())
                    result.append(".");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',result.toString()));
            }
            else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c"+"That Client doesn't exist or is not connected to the VecLinkServer."));
            }
            return true;
        }
        int i = ConnectedVecLinkClient.CFC.values().stream().mapToInt(ConnectedVecLinkClient::getOnlinePlayerCount).sum() + Bukkit.getOnlinePlayers().size();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aTotal players: &f"+ i));
        for(ConnectedVecLinkClient c : ConnectedVecLinkClient.CFC.values()){
            StringBuilder result = new StringBuilder("&a[" + c.getName() + "] &6(&f" + c.getOnlinePlayerCount() + "&6):&f ");
            for(String s : c.playersByName.keySet()){
                result.append(s).append(",");
            }
            result.deleteCharAt(result.length()-1);
            if(!c.playersByName.isEmpty())
                result.append(".");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',result.toString()));
        }
        //send yours
        StringBuilder result = new StringBuilder("&a[" + Main.config.getMainConfig().getString("client-id") + "] &6(&f"
                + Bukkit.getOnlinePlayers().size() + "&6):&f ");
        for(Player p : Bukkit.getOnlinePlayers()){
            result.append(p.getName()).append(",");
        }
        result.deleteCharAt(result.length()-1);
        if(!Bukkit.getOnlinePlayers().isEmpty())
            result.append(".");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',result.toString()));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return VecLinkCommand.sortedResults(args[0], new ArrayList<>(ConnectedVecLinkClient.clients));
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.list";
    }
}
