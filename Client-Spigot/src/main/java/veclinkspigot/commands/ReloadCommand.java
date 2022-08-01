package veclinkspigot.commands;

import veclinkspigot.commands.handler.SubCommand;
import veclinkspigot.VecLinkMainSpigot;
import dev.MrFlyn.veclink.ConfigPath;
import dev.MrFlyn.veclink.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand {

    public ReloadCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.RELOAD_RESPONSE.toString()));
        Main.client.setReconnect(false);
        if(Main.client.channel!=null)
            Main.client.channel.close();
        sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.DISCONNECTED.toString()));
        VecLinkMainSpigot.plugin.onReload();
        Main.client.setReconnect(true);
        try {
            Main.client.createBootstrap(Main.client.group);
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.RECONNECTED.toString()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.ERROR.toString()));
        }
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.reload";
    }
}
