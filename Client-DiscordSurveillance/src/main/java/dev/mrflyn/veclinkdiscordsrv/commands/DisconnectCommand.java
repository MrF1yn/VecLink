package dev.mrflyn.veclinkdiscordsrv.commands;


import com.velocitypowered.api.command.CommandSource;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkvelocity.commands.handler.SubCommand;
import dev.mrflyn.veclinkvelocity.utils.ExtraUtil;

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
        return "veclink.command.disconnect";
    }
}
