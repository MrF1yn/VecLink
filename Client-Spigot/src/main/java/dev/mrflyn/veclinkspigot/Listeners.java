package dev.mrflyn.veclinkspigot;

import dev.mrflyn.veclink.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getLCS;
import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getMiniMessage;

public class Listeners implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(Main.client.channel==null||!(Main.client.channel.isActive()))return;
        Main.client.channel.writeAndFlush(PacketFormatterSpigot.dcChatMonitor(
                getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(e.getPlayer(),
                                VecLinkMainSpigot.plugin.dcChatMonitorFormat)))
                        .replace("[message]",e.getMessage()).replace("[player]",e.getPlayer().getName())
        ));
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(Main.client.channel==null||!(Main.client.channel.isActive()))return;
        VecLinkMainSpigot.plugin.playerChatGroupStatus.put(e.getPlayer(), new ArrayList<>(VecLinkMainSpigot.plugin.chatGroups.keySet()));
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getName();
        int i = Bukkit.getOnlinePlayers().size();
//        if(RemotePlayer.allRemotePlayers.containsKey(name+":"+uuid.toString())){
//            RemotePlayer.allRemotePlayers.get(name+":"+uuid.toString()).getConnectedClients().add(Main.config.getClientID());
//        }else {
//            RemotePlayer p = new RemotePlayer(name, uuid);
//            p.getConnectedClients().add(Main.config.getClientID());
//            RemotePlayer.allRemotePlayers.put(name + ":" + uuid.toString(), p);
//        }
        Bukkit.getScheduler().runTaskAsynchronously(VecLinkMainSpigot.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatPlayerInfoPacket(uuid,name, "ADD", i,
            VecLinkMainSpigot.plugin.isJoinable()));
            Main.gi.debug("Sent player-info packet to VecLinkServer.");
        });
        String targetPlayerName = ((PacketHandlerSpigot)Main.pi).getFindPlayerCache().get(name);
        if (targetPlayerName==null)return;
        Player tPlayer = Bukkit.getPlayer(targetPlayerName);
        if (tPlayer==null)return;
        e.getPlayer().teleport(tPlayer);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (Main.client.channel == null || !(Main.client.channel.isActive())) return;
        VecLinkMainSpigot.plugin.playerChatGroupStatus.remove(e.getPlayer());
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getName();
        int i = Bukkit.getOnlinePlayers().size()-1;
//        if(RemotePlayer.allRemotePlayers.containsKey(name+":"+uuid.toString())){
//            RemotePlayer p = RemotePlayer.allRemotePlayers.get(name+":"+uuid.toString());
//            p.getConnectedClients().remove(Main.config.getClientID());
//            if(p.getConnectedClients().isEmpty()){
//                RemotePlayer.allRemotePlayers.remove(name+":"+uuid.toString());
//            }
//        }
        Bukkit.getScheduler().runTaskAsynchronously(VecLinkMainSpigot.plugin, () -> {
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatPlayerInfoPacket(uuid, name, "REMOVE", i,
                    VecLinkMainSpigot.plugin.isJoinable()) );
            Main.gi.debug("Sent player-info packet to VecLinkServer.");
        });
    }


}
