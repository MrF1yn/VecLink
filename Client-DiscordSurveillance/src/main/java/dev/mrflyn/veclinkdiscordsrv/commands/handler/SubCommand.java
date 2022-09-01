package dev.mrflyn.veclinkdiscordsrv.commands.handler;


import java.util.List;

public interface SubCommand {

     boolean onSubCommand(String sender, String cmd,String[] args);

     String getName();

     boolean isProtected();

     String getPermission();
}
