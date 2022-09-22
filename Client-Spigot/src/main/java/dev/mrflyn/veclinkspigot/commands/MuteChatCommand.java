package dev.mrflyn.veclinkspigot.commands;

import dev.mrflyn.veclinkspigot.commands.handler.VecLinkCommand;
import dev.mrflyn.veclinkspigot.commands.handler.SubCommand;
import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MuteChatCommand implements SubCommand {

    public MuteChatCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(Main.client.channel==null||!(Main.client.channel.isActive())){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString()));
            return true;
        }
        if(args.length<1){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_MUTECHAT.toString()));
            return true;
        }
        String grp = args[0];
        if(!VecLinkMainSpigot.plugin.chatGroups.containsKey(grp)){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CHATGROUP_NOT_EXISTS.toString()));
            return true;
        }
        if((!sender.hasPermission("veclink.chatGroup."+grp))&&!sender.hasPermission("veclink.admin")){
            sender.sendMessage(ConfigPath.NO_PERM.toString());
            return true;
        }
        if(VecLinkMainSpigot.plugin.playerChatGroupStatus.get(p).remove(grp)){
            //MUTED
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CHAT_GRP_MUTED.toString()));
            return true;
        }
        VecLinkMainSpigot.plugin.playerChatGroupStatus.get(p).add(grp);
        //UNMUTED
        sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CHAT_GRP_UNMUTED.toString()));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> results = new ArrayList<>(VecLinkMainSpigot.plugin.chatGroups.keySet());
            return VecLinkCommand.sortedResults(args[0], results);
        }
        return null;
    }

    @Override
    public String getName() {
        return "muteChat";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.muteChat";
    }
}
