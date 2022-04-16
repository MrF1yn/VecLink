package FalconClientSpigot.commands;

import FalconClientSpigot.commands.handler.SubCommand;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DisconnectCommand implements SubCommand {

    public DisconnectCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Main.client.setReconnect(false);
        if(Main.client.channel!=null)
            Main.client.channel.close();
        sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.DISCONNECTED.toString()));

        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "falcon.command.disconnect";
    }
}
