package veclinkvelocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import dev.MrFlyn.veclink.Main;

import java.util.Random;
import java.util.UUID;

import static veclinkvelocity.VecLinkMainVelocity.getPingHandler;

public class Listeners{
    Random random = new Random();
    @Subscribe
    public void onJoin(PostLoginEvent e){
        if(Main.client.channel==null||!(Main.client.channel.isActive()))return;
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getUsername();
        int i = VecLinkMainVelocity.plugin.server.getPlayerCount();
        VecLinkMainVelocity.plugin.server.getScheduler().buildTask(VecLinkMainVelocity.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatPlayerInfoPacket(uuid,name, "ADD", i,
            VecLinkMainVelocity.plugin.isJoinable()));
            Main.gi.debug("Sent player-info packet to VecLinkServer.");
        }).schedule();
    }

    @Subscribe
    public void onLeave(DisconnectEvent e) {
        if (Main.client.channel == null || !(Main.client.channel.isActive())) return;
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getUsername();
        int i = VecLinkMainVelocity.plugin.server.getPlayerCount();
        VecLinkMainVelocity.plugin.server.getScheduler().buildTask(VecLinkMainVelocity.plugin, () -> {
            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatPlayerInfoPacket(uuid, name, "REMOVE", i,
                    VecLinkMainVelocity.plugin.isJoinable()));
            Main.gi.debug("Sent player-info packet to VecLinkServer.");
        }).schedule();
    }

    @Subscribe
    public void onPing(ProxyPingEvent e){
        e.setPing(getPingHandler().handlePing(e.getPing()));
    }


}
