package dev.mrflyn.veclinkvelocity.commands;


import dev.mrflyn.veclinkvelocity.VecLinkMainVelocity;
import dev.mrflyn.veclinkvelocity.commands.handler.SubCommand;
import dev.mrflyn.veclinkvelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;


import java.util.List;

public class ReloadCommand implements SubCommand {

    public ReloadCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.RELOAD_RESPONSE.toString())));
        Main.client.setReconnect(false);
        if(Main.client.channel!=null)
            Main.client.channel.close();
        sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.DISCONNECTED.toString())));
        VecLinkMainVelocity.plugin.onReload();
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
