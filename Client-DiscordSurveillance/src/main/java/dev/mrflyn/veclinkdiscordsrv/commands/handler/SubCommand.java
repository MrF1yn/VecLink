package dev.mrflyn.veclinkdiscordsrv.commands.handler;


import com.velocitypowered.api.command.CommandSource;

import java.util.List;

public interface SubCommand {

     boolean onSubCommand(CommandSource sender, String[] args);

     List<String> suggestTabCompletes(CommandSource sender, String[] args);

     String getName();

     boolean isProtected();

     String getPermission();
}
