package FalconClientVelocity.commands;


import FalconClientVelocity.commands.handler.SubCommand;
import FalconClientVelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;


import java.util.List;

public class ReconnectCommand implements SubCommand {

    public ReconnectCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        if(Main.client.channel!=null&&Main.client.channel.isActive()){
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.ALREADY_CONNECTED.toString())));
            return true;
        }
        Main.client.setReconnect(true);
        try {
            Main.client.createBootstrap(Main.client.group);
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.RECONNECTED.toString())));
        } catch (InterruptedException e) {
            e.printStackTrace();
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.ERROR.toString())));
        }
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
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
