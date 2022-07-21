package FalconClientSpigot;

import dev.MrFlyn.FalconClient.ClientHandlers.RemotePlayer;
import dev.MrFlyn.FalconClient.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class Listeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(Main.client.channel==null||!(Main.client.channel.isActive()))return;
        FalconMainSpigot.plugin.playerChatGroupStatus.put(e.getPlayer(), new ArrayList<>(FalconMainSpigot.plugin.chatGroups.keySet()));
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
        Bukkit.getScheduler().runTaskAsynchronously(FalconMainSpigot.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatPlayerInfoPacket(uuid,name, "ADD", i,
            FalconMainSpigot.plugin.isJoinable())+"\n");
            Main.gi.debug("Sent player-info packet to FalconCloudServer.");
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (Main.client.channel == null || !(Main.client.channel.isActive())) return;
        FalconMainSpigot.plugin.playerChatGroupStatus.remove(e.getPlayer());
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
        Bukkit.getScheduler().runTaskAsynchronously(FalconMainSpigot.plugin, () -> {
            Main.client.channel.writeAndFlush(PacketFormatterSpigot.formatPlayerInfoPacket(uuid, name, "REMOVE", i,
                    FalconMainSpigot.plugin.isJoinable()) + "\n");
            Main.gi.debug("Sent player-info packet to FalconCloudServer.");
        });
    }


}
