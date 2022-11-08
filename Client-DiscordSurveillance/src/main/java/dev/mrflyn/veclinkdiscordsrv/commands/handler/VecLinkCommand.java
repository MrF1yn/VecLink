package dev.mrflyn.veclinkdiscordsrv.commands.handler;


import dev.mrflyn.veclink.Main;


import java.util.*;

public class VecLinkCommand {
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


    public boolean onCommand(String sender, String cmd, String[] args) {
        SubCommand subCommand = registeredSubCommands.get(cmd);
        if (subCommand==null){
            Main.gi.log("Command: "+cmd+" NOT FOUND!");
            return true;
        }
        List<String> fargs = new ArrayList<>(Arrays.asList(args));
        return subCommand.onSubCommand(sender,cmd,fargs.toArray(new String[0]));
    }

    public static void processCommand(VecLinkCommand handler,String sender, String command){
        if (command==null)return;
        command = command.trim();
        Main.gi.log(sender+" executed the command: "+command+".");
        String[] tokens = command.split(" ");
        String cmd = tokens[0];
        String[] args = new ArrayList<>(Arrays.asList(tokens)).subList(1, tokens.length).toArray(new String[0]);
        handler.onCommand(sender, cmd, args);
    }

}

