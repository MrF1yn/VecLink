package FalconClientSpigot.commands;

import FalconClientSpigot.commands.handler.FalconCommand;
import FalconClientSpigot.commands.handler.SubCommand;
import FalconClientSpigot.FalconMainSpigot;
import FalconClientSpigot.PacketFormatterSpigot;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RemoteCommand implements SubCommand {

    public RemoteCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(Main.client.channel==null||!(Main.client.channel.isActive())){

            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString()));
            return true;
        }
        //falcon remoteCmd [serverName|group:] [@c(console)|playerName] [command]
        if(args.length<1){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_REMOTE_CMD.toString()));

            return true;
        }
        String targetSrv = args[0];
        if(args.length<2){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_REMOTE_CMD.toString()));
            return true;
        }
        String executor = args[1];
        if(args.length<3){
            sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.CORRECT_FORMAT_REMOTE_CMD.toString()));
            return true;
        }
        StringBuilder command = new StringBuilder();
        for(int i = 2; i<args.length; i++){
            command.append(args[i]).append(" ");
        }
        Bukkit.getScheduler().runTaskAsynchronously(FalconMainSpigot.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatRemoteCommandPacket(targetSrv,executor,command.toString())+"\n");
        });
        sender.sendMessage(Main.config.getLanguageConfig().getString(ConfigPath.REMOTE_CMD_SENT.toString()));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.addAll(ConnectedFalconClient.clients);
            for (String s : ConnectedFalconClient.groups) {
                results.add("group:" + s);
            }
            return FalconCommand.sortedResults(args[0], results);
        }
        if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach((player -> results.add(player.getName())));
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
