package veclinkspigot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.MrFlyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class TabComplete implements TabCompleter {

    List < String > results = new ArrayList < > ();

    @Override
    public List < String > onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            results.clear();
            results.add("reconnect");
            results.add("disconnect");
            results.add("command");
            results.add("status");
            results.add("chatGroup");
            results.add("muteChat");
            return sortedResults(args[0]);
        }
        else if(args.length==2){
            if(args[0].equalsIgnoreCase("command")){
                results.clear();
                results.addAll(ConnectedVecLinkClient.clients);
                for(String s : ConnectedVecLinkClient.groups){
                    results.add("group:"+s);
                }
                return sortedResults(args[1]);
            }else if(args[0].equalsIgnoreCase("status")){
                results.clear();
                results.addAll(ConnectedVecLinkClient.clients);
//                for(String s : ConnectedVecLinkClient.groups){
//                    results.add("group:"+s);
//                }
                return sortedResults(args[1]);
            }else if(args[0].equalsIgnoreCase("chatGroup")){
                results.clear();
                results.addAll(VecLinkMainSpigot.plugin.chatGroups.keySet());
//                for(String s : ConnectedVecLinkClient.groups){
//                    results.add("group:"+s);
//                }
                return sortedResults(args[1]);
            }else if(args[0].equalsIgnoreCase("muteChat")){
                results.clear();
                results.addAll(VecLinkMainSpigot.plugin.chatGroups.keySet());
//                for(String s : ConnectedVecLinkClient.groups){
//                    results.add("group:"+s);
//                }
                return sortedResults(args[1]);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("command")) {
                results.clear();
                Bukkit.getOnlinePlayers().forEach((player -> results.add(player.getName())));
                results.add("@c");
                return sortedResults(args[2]);
            }
            else if (args[0].equalsIgnoreCase("chatGroup")) {
                results.clear();
                results.add("<message>");
                return sortedResults(args[2]);
            }

        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("command")) {
                results.clear();
                results.add("<command>");
                return sortedResults(args[3]);
            }
        }
        return null;
    }

    // Sorts possible results to provide true tab auto complete based off of what is already typed.
    public List < String > sortedResults(String arg) {
        final List < String > completions = new ArrayList < > ();
        StringUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        results.clear();
        results.addAll(completions);
        return results;
    }

}