package FalconClientSpigot.commands;

import FalconClientSpigot.commands.handler.SubCommand;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReconnectCommand implements SubCommand {

    public ReconnectCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(Main.client.channel!=null&&Main.client.channel.isActive()){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.ALREADY_CONNECTED.toString()));
            return true;
        }
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
        return "reconnect";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "falcon.command.reconnect";
    }
}
