package dev.mrflyn.veclinkdiscordsrv.commands;


import com.velocitypowered.api.command.CommandSource;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkvelocity.PacketFormatterVelocity;
import dev.mrflyn.veclinkvelocity.VecLinkMainVelocity;
import dev.mrflyn.veclinkvelocity.commands.handler.SubCommand;
import dev.mrflyn.veclinkvelocity.commands.handler.VecLinkCommand;
import dev.mrflyn.veclinkvelocity.utils.ExtraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteCommand implements SubCommand {

    public RemoteCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        if(Main.client.channel==null||!(Main.client.channel.isActive())){

            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString())));
            return true;
        }
        //veclink remoteCmd [serverName|group:] [@c(console)|playerName] [command]
        if(args.length<1){
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_REMOTE_CMD.toString())));

            return true;
        }
        String targetSrv = args[0];
        if(args.length<2){
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_REMOTE_CMD.toString())));
            return true;
        }
        String executor = args[1];
        if(args.length<3){
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_REMOTE_CMD.toString())));
            return true;
        }
        StringBuilder command = new StringBuilder();
        for(int i = 2; i<args.length; i++){
            command.append(args[i]).append(" ");
        }
        VecLinkMainVelocity.plugin.server.getScheduler().buildTask(VecLinkMainVelocity.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatRemoteCommandPacket(targetSrv,executor,command.toString()));
        }).delay(0, TimeUnit.MILLISECONDS).schedule();
        sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.REMOTE_CMD_SENT.toString())));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.addAll(ConnectedVecLinkClient.clients);
            for (String s : ConnectedVecLinkClient.groups) {
                results.add("group:" + s);
            }
            return VecLinkCommand.sortedResults(args[0], results);
        }
        if (args.length == 2) {
            VecLinkMainVelocity.plugin.server.getAllPlayers().forEach((player -> results.add(player.getUsername())));
            results.add("@c");
            return VecLinkCommand.sortedResults(args[1], results);
        }
        else if (args.length == 3) {
            results.add("<command>");
            return VecLinkCommand.sortedResults(args[2], results);
        }
        return null;
    }

    @Override
    public String getName() {
        return "remoteCmd";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.remoteCmd";
    }
}
