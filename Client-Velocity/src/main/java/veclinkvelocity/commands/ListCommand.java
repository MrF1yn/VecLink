package veclinkvelocity.commands;


import veclinkvelocity.VecLinkMainVelocity;
import veclinkvelocity.commands.handler.VecLinkCommand;
import veclinkvelocity.commands.handler.SubCommand;
import veclinkvelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.MrFlyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.MrFlyn.veclink.Main;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements SubCommand {
    public ListCommand(){
    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        if(args.length==1){
            String name = args[0];
            if(ConnectedVecLinkClient.CFC.containsKey(name)){
                ConnectedVecLinkClient c = ConnectedVecLinkClient.CFC.get(name);
                StringBuilder result = new StringBuilder("&a[" + c.getName() + "] &6(&f" + c.getOnlinePlayerCount() + "&6):&f ");
                for(String s : c.playersByName.keySet()){
                    result.append(s).append(",");
                }
                result.deleteCharAt(result.length()-1);
                if(!c.playersByName.isEmpty())
                    result.append(".");
                sender.sendMessage(ExtraUtil.color(result.toString()));
            }
            else if(name.equals(Main.config.getMainConfig().getString("client-id"))){
                StringBuilder result = new StringBuilder("&a[" + name + "] &6(&f" + VecLinkMainVelocity.plugin.server.getPlayerCount() + "&6):&f ");
                for(Player p : VecLinkMainVelocity.plugin.server.getAllPlayers()){
                    result.append(p.getUsername()).append(",");
                }
                result.deleteCharAt(result.length()-1);
                if(VecLinkMainVelocity.plugin.server.getPlayerCount()>0)
                    result.append(".");
                sender.sendMessage(ExtraUtil.color(result.toString()));
            }
            else {
                sender.sendMessage(ExtraUtil.color("&c"+"That Client doesn't exist or is not connected to the VecLinkServer."));
            }
            return true;
        }
        int i = ConnectedVecLinkClient.CFC.values().stream().mapToInt(ConnectedVecLinkClient::getOnlinePlayerCount).sum();
        sender.sendMessage(ExtraUtil.color("&aTotal players(without including this proxy): &f"+ i));
        sender.sendMessage(ExtraUtil.color("&aTotal players(with including this proxy): &f"+ (i+ VecLinkMainVelocity.plugin.server.getPlayerCount())));
        for(ConnectedVecLinkClient c : ConnectedVecLinkClient.CFC.values()){
            StringBuilder result = new StringBuilder("&a[" + c.getName() + "] &6(&f" + c.getOnlinePlayerCount() + "&6):&f ");
            for(String s : c.playersByName.keySet()){
                result.append(s).append(",");
            }
            result.deleteCharAt(result.length()-1);
            if(!c.playersByName.isEmpty())
                result.append(".");
            sender.sendMessage(ExtraUtil.color(result.toString()));
        }
        //send yours
        StringBuilder result = new StringBuilder("&a[" + Main.config.getMainConfig().getString("client-id") + "] &6(&f"
                + VecLinkMainVelocity.plugin.server.getPlayerCount() + "&6):&f ");
        for(Player p : VecLinkMainVelocity.plugin.server.getAllPlayers()){
            result.append(p.getUsername()).append(",");
        }
        result.deleteCharAt(result.length()-1);
        if(VecLinkMainVelocity.plugin.server.getPlayerCount()>0)
            result.append(".");
        sender.sendMessage(ExtraUtil.color(result.toString()));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
        return VecLinkCommand.sortedResults(args[0], new ArrayList<>(ConnectedVecLinkClient.clients));
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.list";
    }
}
