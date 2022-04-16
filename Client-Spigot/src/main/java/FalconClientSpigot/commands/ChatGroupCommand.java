package FalconClientSpigot.commands;

import FalconClientSpigot.commands.handler.FalconCommand;
import FalconClientSpigot.commands.handler.SubCommand;
import FalconClientSpigot.FalconMainSpigot;
import FalconClientSpigot.PacketFormatterSpigot;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatGroupCommand implements SubCommand {

    public ChatGroupCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(Main.client.channel==null||!(Main.client.channel.isActive())){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString()));
            return true;
        }
        if(args.length<2){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_CHATGROUP.toString()));
            return true;
        }
        String grpName = args[0];
        if((!sender.hasPermission("falcon.chatGroup."+grpName))&&!sender.hasPermission("falcon.admin")){
            sender.sendMessage(ConfigPath.NO_PERM.toString());
            return true;
        }
        if(!FalconMainSpigot.plugin.chatGroups.containsKey(grpName)){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CHATGROUP_NOT_EXISTS.toString()));
        }
        StringBuilder message = new StringBuilder();
        for(int i = 1; i<args.length; i++){
            message.append(args[i]).append(" ");
        }
        //SEND CHAT GRP
        String finalmsg;
        if(sender instanceof Player) {
            Player p = (Player) sender;
            finalmsg = ChatColor.translateAlternateColorCodes('&',
                            FalconMainSpigot.PAPIparseIfAvailable(p, FalconMainSpigot.plugin.chatGroups.get(grpName)))
                    .replace("[message]", message).replace("[player]", p.getName()).replace("[client-id]",
                            Main.config.getMainConfig().getString("client-id"));
        }
        else {
            finalmsg = ChatColor.translateAlternateColorCodes('&',
                            FalconMainSpigot.plugin.chatGroups.get(grpName))
                    .replace("[message]", message).replace("[player]", sender.getName()).replace("[client-id]",
                            Main.config.getMainConfig().getString("client-id"));
        }
        Bukkit.getScheduler().runTaskAsynchronously(FalconMainSpigot.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.chatGroupMessage(finalmsg, sender.getName(),grpName)+"\n");
        });
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.addAll(FalconMainSpigot.plugin.chatGroups.keySet());
            return FalconCommand.sortedResults(args[0], results);
        }
        if (args.length == 2) {
            results.add("<message>");
            return FalconCommand.sortedResults(args[1], results);
        }
        return null;
    }

    @Override
    public String getName() {
        return "chatGroup";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "falcon.command.chatGroup";
    }
}
