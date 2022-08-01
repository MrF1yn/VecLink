package veclinkspigot.commands;

import veclinkspigot.commands.handler.VecLinkCommand;
import veclinkspigot.commands.handler.SubCommand;
import veclinkspigot.VecLinkMainSpigot;
import veclinkspigot.utils.MemoryUtil;
import veclinkspigot.utils.SpigotReflection;
import dev.MrFlyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.MrFlyn.veclink.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatusCommand implements SubCommand {
    DecimalFormat decimalFormat;
    public StatusCommand(){
        decimalFormat = new DecimalFormat("0.00");
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length==1){
            String name = args[0];
            if(ConnectedVecLinkClient.CFC.containsKey(name)){
                ConnectedVecLinkClient c = ConnectedVecLinkClient.CFC.get(name);
                sender.sendMessage(ChatColor.WHITE + "↱"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↰");
//                sender.sendMessage(ChatColor.AQUA +"ClientName: "+ChatColor.WHITE+c.getName());
//                sender.sendMessage(ChatColor.AQUA+"ClientType: "+ChatColor.WHITE+c.getType());
//                sender.sendMessage(ChatColor.AQUA+"BelongingGroups: "+ChatColor.WHITE+c.getGroups());
//                sender.sendMessage(ChatColor.AQUA+"Is-Joinable: "+ChatColor.WHITE+c.isCanJoin());
//                sender.sendMessage(ChatColor.AQUA+"TotalPlayers: "+ChatColor.WHITE+c.getOnlinePlayerCount());
//                if(c.getType().equals("SPIGOT")) {
//                    sender.sendMessage(ChatColor.AQUA+"Mspt: " + ChatColor.WHITE+decimalFormat.format(c.getMspt()));
//                    sender.sendMessage(ChatColor.AQUA+"Tps 1m,5m,15m: " + ChatColor.WHITE+decimalFormat.format(c.getTps1min())+","+
//                            decimalFormat.format(c.getTps5min())+","+ decimalFormat.format(c.getTps15min()));
//                }
//                sender.sendMessage(ChatColor.AQUA+"OperatingSystem: "+ChatColor.WHITE+c.getOsName());
//                sender.sendMessage(ChatColor.AQUA+"RunningThreads: "+ChatColor.WHITE+c.getRunningThreads());
//                sender.sendMessage(ChatColor.AQUA+"No.of CPU cores: "+ChatColor.WHITE+c.getCpuCores());
//                sender.sendMessage(ChatColor.AQUA+"CpuUsage: "+ChatColor.WHITE+decimalFormat.format(c.getCpuUsagePercent())+"%");
//                sender.sendMessage(ChatColor.AQUA+"MemoryUsage: "+ChatColor.WHITE+decimalFormat.format(c.getMemoryUsagePercent())+"% ("
//                        +decimalFormat.format(c.getAllocatedMemory())+"/"+decimalFormat.format(c.getMaxMemory())+" MB)");
//                sender.sendMessage(ChatColor.AQUA+"AllocatedMemory: "+ChatColor.WHITE+decimalFormat.format(c.getCurrentMemoryUsage())+"MB");
                for(String s : c.getFormattedClientInfo()){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
                sender.sendMessage(ChatColor.WHITE + "↳"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↲");
            }
            else if(name.equals(Main.config.getMainConfig().getString("client-id"))){
                sender.sendMessage(ChatColor.WHITE + "↱"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↰");
                sender.sendMessage(ChatColor.AQUA + "ClientName: " + ChatColor.WHITE + Main.config.getMainConfig().getString("client-id"));
                sender.sendMessage(ChatColor.AQUA + "ClientType: " + ChatColor.WHITE + "SPIGOT");
                sender.sendMessage(ChatColor.AQUA + "BelongingGroups: " + ChatColor.WHITE + VecLinkMainSpigot.plugin.groups);
                sender.sendMessage(ChatColor.AQUA + "Is-Joinable: " + ChatColor.WHITE + VecLinkMainSpigot.plugin.isJoinable());

                sender.sendMessage(ChatColor.AQUA + "TotalPlayers: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
                sender.sendMessage(ChatColor.AQUA + "Mspt: " + ChatColor.WHITE + decimalFormat.format(SpigotReflection.get().averageTickTime()));
                sender.sendMessage(ChatColor.AQUA + "Tps 1m,5m,15m: " + ChatColor.WHITE + decimalFormat.format(SpigotReflection.get().recentTps()[0]) + "," +
                        decimalFormat.format(SpigotReflection.get().recentTps()[1]) + "," + decimalFormat.format(SpigotReflection.get().recentTps()[2]));

                sender.sendMessage(ChatColor.AQUA + "OperatingSystem: " + ChatColor.WHITE + MemoryUtil.getOsName());
                sender.sendMessage(ChatColor.AQUA + "RunningThreads: " + ChatColor.WHITE + MemoryUtil.getFormattedMemory()[0]);
                sender.sendMessage(ChatColor.AQUA + "No.of CPU cores: " + ChatColor.WHITE + MemoryUtil.getFormattedMemory()[1]);
                sender.sendMessage(ChatColor.AQUA + "CpuUsage: " + ChatColor.WHITE + decimalFormat.format(MemoryUtil.getProcessCpuLoad()) + "%");
                sender.sendMessage(ChatColor.AQUA + "MemoryUsage: " + ChatColor.WHITE + decimalFormat.format(MemoryUtil.getFormattedMemory()[3]) + "% ("
                        + decimalFormat.format(MemoryUtil.getFormattedMemory()[6]) + "/" + decimalFormat.format(MemoryUtil.getFormattedMemory()[5]) + " MB)");
                sender.sendMessage(ChatColor.AQUA + "AllocatedMemory: " + ChatColor.WHITE + decimalFormat.format(MemoryUtil.getFormattedMemory()[4]) + "MB");
                sender.sendMessage(ChatColor.WHITE + "↳"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↲");
                sender.sendMessage(" ");
            }
            else {
                sender.sendMessage(ChatColor.RED+"That Client doesn't exist or is not connected to the VecLinkServer.");
            }
            return true;
        }
        for(ConnectedVecLinkClient c : ConnectedVecLinkClient.CFC.values()){
            sender.sendMessage(ChatColor.WHITE + "↱"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↰");
//            sender.sendMessage(ChatColor.AQUA +"ClientName: "+ChatColor.WHITE+c.getName());
//            sender.sendMessage(ChatColor.AQUA+"ClientType: "+ChatColor.WHITE+c.getType());
//            sender.sendMessage(ChatColor.AQUA+"BelongingGroups: "+ChatColor.WHITE+c.getGroups());
//            sender.sendMessage(ChatColor.AQUA+"Is-Joinable: "+ChatColor.WHITE+c.isCanJoin());
//            sender.sendMessage(ChatColor.AQUA+"TotalPlayers: "+ChatColor.WHITE+c.getOnlinePlayerCount());
//            if(c.getType().equals("SPIGOT")) {
//                sender.sendMessage(ChatColor.AQUA+"Mspt: " + ChatColor.WHITE+decimalFormat.format(c.getMspt()));
//                sender.sendMessage(ChatColor.AQUA+"Tps 1m,5m,15m: " + ChatColor.WHITE+decimalFormat.format(c.getTps1min())+","+
//                        decimalFormat.format(c.getTps5min())+","+ decimalFormat.format(c.getTps15min()));
//            }
//            sender.sendMessage(ChatColor.AQUA+"OperatingSystem: "+ChatColor.WHITE+c.getOsName());
//            sender.sendMessage(ChatColor.AQUA+"RunningThreads: "+ChatColor.WHITE+c.getRunningThreads());
//            sender.sendMessage(ChatColor.AQUA+"No.of CPU cores: "+ChatColor.WHITE+c.getCpuCores());
//            sender.sendMessage(ChatColor.AQUA+"CpuUsage: "+ChatColor.WHITE+decimalFormat.format(c.getCpuUsagePercent())+"%");
//            sender.sendMessage(ChatColor.AQUA+"MemoryUsage: "+ChatColor.WHITE+decimalFormat.format(c.getMemoryUsagePercent())+"% ("
//                    +decimalFormat.format(c.getAllocatedMemory())+"/"+decimalFormat.format(c.getMaxMemory())+" MB)");
//            sender.sendMessage(ChatColor.AQUA+"AllocatedMemory: "+ChatColor.WHITE+decimalFormat.format(c.getCurrentMemoryUsage())+"MB");
            for(String s : c.getFormattedClientInfo()){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
            sender.sendMessage(ChatColor.WHITE + "↳"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↲");
            sender.sendMessage(" ");
        }
        sender.sendMessage(ChatColor.WHITE + "↱"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↰");
        sender.sendMessage(ChatColor.AQUA + "ClientName: " + ChatColor.WHITE + Main.config.getMainConfig().getString("client-id"));
        sender.sendMessage(ChatColor.AQUA + "ClientType: " + ChatColor.WHITE + "SPIGOT");
        sender.sendMessage(ChatColor.AQUA + "BelongingGroups: " + ChatColor.WHITE + VecLinkMainSpigot.plugin.groups);
        sender.sendMessage(ChatColor.AQUA + "Is-Joinable: " + ChatColor.WHITE + VecLinkMainSpigot.plugin.isJoinable());

        sender.sendMessage(ChatColor.AQUA + "TotalPlayers: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        sender.sendMessage(ChatColor.AQUA + "Mspt: " + ChatColor.WHITE + decimalFormat.format(SpigotReflection.get().averageTickTime()));
        sender.sendMessage(ChatColor.AQUA + "Tps 1m,5m,15m: " + ChatColor.WHITE + decimalFormat.format(SpigotReflection.get().recentTps()[0]) + "," +
                decimalFormat.format(SpigotReflection.get().recentTps()[1]) + "," + decimalFormat.format(SpigotReflection.get().recentTps()[2]));

        sender.sendMessage(ChatColor.AQUA + "OperatingSystem: " + ChatColor.WHITE + MemoryUtil.getOsName());
        sender.sendMessage(ChatColor.AQUA + "RunningThreads: " + ChatColor.WHITE + MemoryUtil.getFormattedMemory()[0]);
        sender.sendMessage(ChatColor.AQUA + "No.of CPU cores: " + ChatColor.WHITE + MemoryUtil.getFormattedMemory()[1]);
        sender.sendMessage(ChatColor.AQUA + "CpuUsage: " + ChatColor.WHITE + decimalFormat.format(MemoryUtil.getProcessCpuLoad()) + "%");
        sender.sendMessage(ChatColor.AQUA + "MemoryUsage: " + ChatColor.WHITE + decimalFormat.format(MemoryUtil.getFormattedMemory()[3]) + "% ("
                + decimalFormat.format(MemoryUtil.getFormattedMemory()[6]) + "/" + decimalFormat.format(MemoryUtil.getFormattedMemory()[5]) + " MB)");
        sender.sendMessage(ChatColor.AQUA + "AllocatedMemory: " + ChatColor.WHITE + decimalFormat.format(MemoryUtil.getFormattedMemory()[4]) + "MB");
        sender.sendMessage(ChatColor.WHITE + "↳"+ChatColor.DARK_AQUA+"---------------------------"+ChatColor.WHITE+"↲");
        sender.sendMessage(" ");
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSender sender, Command cmd, String label, String[] args) {
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
