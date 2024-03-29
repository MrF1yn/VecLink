package dev.mrflyn.veclinkvelocity.commands;


import dev.mrflyn.veclinkvelocity.VecLinkMainVelocity;
import dev.mrflyn.veclinkvelocity.commands.handler.VecLinkCommand;
import dev.mrflyn.veclinkvelocity.commands.handler.SubCommand;
import dev.mrflyn.veclinkvelocity.utils.ExtraUtil;
import dev.mrflyn.veclinkvelocity.utils.MemoryUtil;
import com.velocitypowered.api.command.CommandSource;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.Main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatusCommand implements SubCommand {
    DecimalFormat decimalFormat;
    public StatusCommand(){
        decimalFormat = new DecimalFormat("0.00");
    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        if(args.length==1){
            String name = args[0];
            if(ConnectedVecLinkClient.CFC.containsKey(name)){
                ConnectedVecLinkClient c = ConnectedVecLinkClient.CFC.get(name);
                sender.sendMessage(ExtraUtil.color("&f" + "↱"+"&3"+"---------------------------"+"&f"+"↰"));
//                sender.sendMessage(ExtraUtil.color("&b" +"ClientName: "+"&f"+c.getName()));
//                sender.sendMessage(ExtraUtil.color("&b"+"ClientType: "+"&f"+c.getType()));
//                sender.sendMessage(ExtraUtil.color("&b"+"BelongingGroups: "+"&f"+c.getGroups()));
//                sender.sendMessage(ExtraUtil.color("&b"+"Is-Joinable: "+"&f"+c.isCanJoin()));
//                sender.sendMessage(ExtraUtil.color("&b"+"TotalPlayers: "+"&f"+c.getOnlinePlayerCount()));
//                if(c.getType().equals("SPIGOT")) {
//                    sender.sendMessage(ExtraUtil.color("&b"+"Mspt: " + "&f"+decimalFormat.format(c.getMspt())));
//                    sender.sendMessage(ExtraUtil.color("&b"+"Tps 1m,5m,15m: " + "&f"+decimalFormat.format(c.getTps1min())+","+
//                            decimalFormat.format(c.getTps5min())+","+ decimalFormat.format(c.getTps15min())));
//                }
//                sender.sendMessage(ExtraUtil.color("&b"+"OperatingSystem: "+"&f"+c.getOsName()));
//                sender.sendMessage(ExtraUtil.color("&b"+"RunningThreads: "+"&f"+c.getRunningThreads()));
//                sender.sendMessage(ExtraUtil.color("&b"+"No.of CPU cores: "+"&f"+c.getCpuCores()));
//                sender.sendMessage(ExtraUtil.color("&b"+"CpuUsage: "+"&f"+decimalFormat.format(c.getCpuUsagePercent())+"%"));
//                sender.sendMessage(ExtraUtil.color("&b"+"MemoryUsage: "+"&f"+decimalFormat.format(c.getMemoryUsagePercent())+"% ("
//                        +decimalFormat.format(c.getAllocatedMemory())+"/"+decimalFormat.format(c.getMaxMemory())+" MB)"));
//                sender.sendMessage(ExtraUtil.color("&b"+"AllocatedMemory: "+"&f"+decimalFormat.format(c.getCurrentMemoryUsage())+"MB"));
                for(String s : c.getFormattedClientInfo()){
                    sender.sendMessage(ExtraUtil.color(s));
                }
                sender.sendMessage(ExtraUtil.color("&f" + "↳"+"&3"+"---------------------------"+"&f"+"↲"));
            }
            else if(name.equals(Main.config.getMainConfig().getString("client-id"))){
                sender.sendMessage(ExtraUtil.color("&f" + "↱"+"&3"+"---------------------------"+"&f"+"↰"));
                sender.sendMessage(ExtraUtil.color("&b" + "ClientName: " + "&f" + Main.config.getMainConfig().getString("client-id")));
                sender.sendMessage(ExtraUtil.color("&b" + "ClientType: " + "&f" + "VELOCITY"));
                sender.sendMessage(ExtraUtil.color("&b" + "BelongingGroups: " + "&f" + VecLinkMainVelocity.plugin.groups));
                sender.sendMessage(ExtraUtil.color("&b" + "Is-Joinable: " + "&f" + VecLinkMainVelocity.plugin.isJoinable()));

                sender.sendMessage(ExtraUtil.color("&b" + "TotalPlayers: " + "&f" + VecLinkMainVelocity.plugin.server.getPlayerCount()));
                sender.sendMessage(ExtraUtil.color("&b" + "OperatingSystem: " + "&f" + MemoryUtil.getOsName()));
                sender.sendMessage(ExtraUtil.color("&b" + "RunningThreads: " + "&f" + MemoryUtil.getFormattedMemory()[0]));
                sender.sendMessage(ExtraUtil.color("&b" + "No.of CPU cores: " + "&f" + MemoryUtil.getFormattedMemory()[1]));
                sender.sendMessage(ExtraUtil.color("&b" + "CpuUsage: " + "&f" + decimalFormat.format(MemoryUtil.getProcessCpuLoad()) + "%"));
                sender.sendMessage(ExtraUtil.color("&b" + "MemoryUsage: " + "&f" + decimalFormat.format(MemoryUtil.getFormattedMemory()[3]) + "% ("
                        + decimalFormat.format(MemoryUtil.getFormattedMemory()[6]) + "/" + decimalFormat.format(MemoryUtil.getFormattedMemory()[5]) + " MB)"));
                sender.sendMessage(ExtraUtil.color("&b" + "AllocatedMemory: " + "&f" + decimalFormat.format(MemoryUtil.getFormattedMemory()[4]) + "MB"));
                sender.sendMessage(ExtraUtil.color("&f" + "↳"+"&3"+"---------------------------"+"&f"+"↲"));
                sender.sendMessage(ExtraUtil.color(" "));
            }
            else {
                sender.sendMessage(ExtraUtil.color("&c"+"That Client doesn't exist or is not connected to the VecLinkServer."));
            }
            return true;
        }
        for(ConnectedVecLinkClient c : ConnectedVecLinkClient.CFC.values()){
            sender.sendMessage(ExtraUtil.color("&f" + "↱"+"&3"+"---------------------------"+"&f"+"↰"));
//            sender.sendMessage(ExtraUtil.color("&b" +"ClientName: "+"&f"+c.getName()));
//            sender.sendMessage(ExtraUtil.color("&b"+"ClientType: "+"&f"+c.getType()));
//            sender.sendMessage(ExtraUtil.color("&b"+"BelongingGroups: "+"&f"+c.getGroups()));
//            sender.sendMessage(ExtraUtil.color("&b"+"Is-Joinable: "+"&f"+c.isCanJoin()));
//            sender.sendMessage(ExtraUtil.color("&b"+"TotalPlayers: "+"&f"+c.getOnlinePlayerCount()));
//            if(c.getType().equals("SPIGOT")) {
//                sender.sendMessage(ExtraUtil.color("&b"+"Mspt: " + "&f"+decimalFormat.format(c.getMspt())));
//                sender.sendMessage(ExtraUtil.color("&b"+"Tps 1m,5m,15m: " + "&f"+decimalFormat.format(c.getTps1min())+","+
//                        decimalFormat.format(c.getTps5min())+","+ decimalFormat.format(c.getTps15min())));
//            }
//            sender.sendMessage(ExtraUtil.color("&b"+"OperatingSystem: "+"&f"+c.getOsName()));
//            sender.sendMessage(ExtraUtil.color("&b"+"RunningThreads: "+"&f"+c.getRunningThreads()));
//            sender.sendMessage(ExtraUtil.color("&b"+"No.of CPU cores: "+"&f"+c.getCpuCores()));
//            sender.sendMessage(ExtraUtil.color("&b"+"CpuUsage: "+"&f"+decimalFormat.format(c.getCpuUsagePercent())+"%"));
//            sender.sendMessage(ExtraUtil.color("&b"+"MemoryUsage: "+"&f"+decimalFormat.format(c.getMemoryUsagePercent())+"% ("
//                    +decimalFormat.format(c.getAllocatedMemory())+"/"+decimalFormat.format(c.getMaxMemory())+" MB)"));
//            sender.sendMessage(ExtraUtil.color("&b"+"AllocatedMemory: "+"&f"+decimalFormat.format(c.getCurrentMemoryUsage())+"MB"));
            for(String s : c.getFormattedClientInfo()){
                sender.sendMessage(ExtraUtil.color(s));
            }
            sender.sendMessage(ExtraUtil.color("&f" + "↳"+"&3"+"---------------------------"+"&f"+"↲"));
            sender.sendMessage(ExtraUtil.color(" "));
        }
        sender.sendMessage(ExtraUtil.color("&f" + "↱"+"&3"+"---------------------------"+"&f"+"↰"));
        sender.sendMessage(ExtraUtil.color("&b" + "ClientName: " + "&f" + Main.config.getMainConfig().getString("client-id")));
        sender.sendMessage(ExtraUtil.color("&b" + "ClientType: " + "&f" + "VELOCITY"));
        sender.sendMessage(ExtraUtil.color("&b" + "BelongingGroups: " + "&f" + VecLinkMainVelocity.plugin.groups));
        sender.sendMessage(ExtraUtil.color("&b" + "Is-Joinable: " + "&f" + VecLinkMainVelocity.plugin.isJoinable()));

        sender.sendMessage(ExtraUtil.color("&b" + "TotalPlayers: " + "&f" + VecLinkMainVelocity.plugin.server.getPlayerCount()));

        sender.sendMessage(ExtraUtil.color("&b" + "OperatingSystem: " + "&f" + MemoryUtil.getOsName()));
        sender.sendMessage(ExtraUtil.color("&b" + "RunningThreads: " + "&f" + MemoryUtil.getFormattedMemory()[0]));
        sender.sendMessage(ExtraUtil.color("&b" + "No.of CPU cores: " + "&f" + MemoryUtil.getFormattedMemory()[1]));
        sender.sendMessage(ExtraUtil.color("&b" + "CpuUsage: " + "&f" + decimalFormat.format(MemoryUtil.getProcessCpuLoad()) + "%"));
        sender.sendMessage(ExtraUtil.color("&b" + "MemoryUsage: " + "&f" + decimalFormat.format(MemoryUtil.getFormattedMemory()[3]) + "% ("
                + decimalFormat.format(MemoryUtil.getFormattedMemory()[6]) + "/" + decimalFormat.format(MemoryUtil.getFormattedMemory()[5]) + " MB)"));
        sender.sendMessage(ExtraUtil.color("&b" + "AllocatedMemory: " + "&f" + decimalFormat.format(MemoryUtil.getFormattedMemory()[4]) + "MB"));
        sender.sendMessage(ExtraUtil.color("&f" + "↳"+"&3"+"---------------------------"+"&f"+"↲"));
        sender.sendMessage(ExtraUtil.color(" "));
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
        return VecLinkCommand.sortedResults(args[0], new ArrayList<>(ConnectedVecLinkClient.clients));
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "veclink.command.status";
    }
}
