package veclinkvelocity.commands.handler;

import veclinkvelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.MrFlyn.veclink.ConfigPath;
import dev.MrFlyn.veclink.Main;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;

public class VecLinkCommand implements SimpleCommand {
    private HashMap<String, SubCommand> registeredSubCommands = new HashMap<>();
    private List <String> results = new ArrayList<>();

    public VecLinkCommand(SubCommand... subCommands){
       for(SubCommand cmd : subCommands){
           registeredSubCommands.put(cmd.getName(), cmd);
       }
    }

    public void registerSubCommand(SubCommand command){
        if(registeredSubCommands.containsKey(command.getName()))return;
        registeredSubCommands.put(command.getName(), command);
    }

    public void unregisterSubCommand(SubCommand command) {
        if (!registeredSubCommands.containsKey(command.getName())) return;
        registeredSubCommands.remove(command.getName());
    }

    public void unregisterAll() {
        registeredSubCommands.clear();
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();
        if(args.length==0||!registeredSubCommands.containsKey(args[0])){
            //doesnt exist
            return;
        }
        SubCommand subCommand = registeredSubCommands.get(args[0]);
        if(subCommand.isProtected()&&!(sender.hasPermission(subCommand.getPermission())||sender.hasPermission("veclink.admin"))) {
            //NO permission
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Main.config.getLanguageConfig().getString(ConfigPath.NO_PERM.toString())));
            return;
        }
        List<String> fargs = new ArrayList<>(Arrays.asList(args));
        fargs.remove(0);
        subCommand.onSubCommand(sender, fargs.toArray(new String[0]));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();
        if (args.length == 1) {
            results.clear();
            results.addAll(registeredSubCommands.keySet());
            return sortedResults(args[0], results);
        }
        if(args.length>1){
            results.clear();
            if(!registeredSubCommands.containsKey(args[0])){
                //doesnt exist
                return results;
            }
            SubCommand subCommand = registeredSubCommands.get(args[0]);
            List<String> fargs = new ArrayList<>(Arrays.asList(args));
            fargs.remove(0);
            return subCommand.suggestTabCompletes(sender,fargs.toArray(new String[0]));
        }

        return null;
    }

    public static List <String> sortedResults(String arg, List<String> results) {
        final List < String > completions = new ArrayList < > ();
        ExtraUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        results.clear();
        results.addAll(completions);
        return results;
    }
}

