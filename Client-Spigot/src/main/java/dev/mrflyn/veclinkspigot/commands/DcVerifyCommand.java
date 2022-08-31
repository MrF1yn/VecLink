package dev.mrflyn.veclinkspigot.commands;

import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkcommon.ClientType;
import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import dev.mrflyn.veclinkspigot.commands.handler.SubCommand;
import dev.mrflyn.veclinkspigot.commands.handler.VecLinkCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DcVerifyCommand implements SubCommand {

    public DcVerifyCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(Main.client.channel==null||!(Main.client.channel.isActive())){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString()));
            return true;
        }
        for(ConnectedVecLinkClient c : ConnectedVecLinkClient.CFC.values()){
        }
        if (!ConnectedVecLinkClient.containsType(ClientType.DISCORD_SRV)) {
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.VECLINK_SRV_NOT_CONNECTED.toString()));
            return true;
        }



        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "dcVerify";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.dcVerify";
    }
}
