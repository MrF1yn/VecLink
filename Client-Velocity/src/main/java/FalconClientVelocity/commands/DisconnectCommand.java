package FalconClientVelocity.commands;


import FalconClientVelocity.commands.handler.SubCommand;
import FalconClientVelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;


import java.util.List;

public class DisconnectCommand implements SubCommand {

    public DisconnectCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        Main.client.setReconnect(false);
        if(Main.client.channel!=null)
            Main.client.channel.close();
        sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.DISCONNECTED.toString())));

        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
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
