package FalconClientVelocity.commands;


import FalconClientVelocity.FalconMainVelocity;
import FalconClientVelocity.PacketFormatterVelocity;
import FalconClientVelocity.commands.handler.FalconCommand;
import FalconClientVelocity.commands.handler.SubCommand;
import FalconClientVelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;

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
        //falcon remoteCmd [serverName|group:] [@c(console)|playerName] [command]
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
        FalconMainVelocity.plugin.server.getScheduler().buildTask(FalconMainVelocity.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatRemoteCommandPacket(targetSrv,executor,command.toString()));
        }).delay(0, TimeUnit.MILLISECONDS).schedule();
        sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.REMOTE_CMD_SENT.toString())));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.addAll(ConnectedFalconClient.clients);
            for (String s : ConnectedFalconClient.groups) {
                results.add("group:" + s);
            }
            return FalconCommand.sortedResults(args[0], results);
        }
        if (args.length == 2) {
            FalconMainVelocity.plugin.server.getAllPlayers().forEach((player -> results.add(player.getUsername())));
            results.add("@c");
            return FalconCommand.sortedResults(args[1], results);
        }
        else if (args.length == 3) {
            results.add("<command>");
            return FalconCommand.sortedResults(args[2], results);
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
        return "falcon.command.remoteCmd";
    }
}
