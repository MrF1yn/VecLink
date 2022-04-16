package FalconClientSpigot.commands;


import FalconClientSpigot.commands.handler.FalconCommand;
import FalconClientSpigot.commands.handler.SubCommand;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.Main;
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
            if(ConnectedFalconClient.CFC.containsKey(name)){
                ConnectedFalconClient c = ConnectedFalconClient.CFC.get(name);
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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c"+"That Client doesn't exist or is not connected to the FalconCloudServer."));
            }
            return true;
        }
        int i = ConnectedFalconClient.CFC.values().stream().mapToInt(ConnectedFalconClient::getOnlinePlayerCount).sum() + Bukkit.getOnlinePlayers().size();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aTotal players: &f"+ i));
        for(ConnectedFalconClient c : ConnectedFalconClient.CFC.values()){
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
        return FalconCommand.sortedResults(args[0], new ArrayList<>(ConnectedFalconClient.clients));
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
        return "falcon.command.list";
    }
}
