package FalconClientVelocity;
import FalconClientVelocity.API.placeholderapinative.PlaceholderAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import dev.MrFlyn.FalconClient.Main;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static FalconClientVelocity.FalconMainVelocity.getPingHandler;

public class Listeners{
    Random random = new Random();
    @Subscribe
    public void onJoin(PostLoginEvent e){
        if(Main.client.channel==null||!(Main.client.channel.isActive()))return;
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getUsername();
        int i = FalconMainVelocity.plugin.server.getPlayerCount();
        FalconMainVelocity.plugin.server.getScheduler().buildTask(FalconMainVelocity.plugin, ()->{
            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatPlayerInfoPacket(uuid,name, "ADD", i,
            FalconMainVelocity.plugin.isJoinable()));
            Main.gi.debug("Sent player-info packet to FalconCloudServer.");
        }).schedule();
    }

    @Subscribe
    public void onLeave(DisconnectEvent e) {
        if (Main.client.channel == null || !(Main.client.channel.isActive())) return;
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getUsername();
        int i = FalconMainVelocity.plugin.server.getPlayerCount();
        FalconMainVelocity.plugin.server.getScheduler().buildTask(FalconMainVelocity.plugin, () -> {
            Main.client.channel.writeAndFlush(PacketFormatterVelocity.formatPlayerInfoPacket(uuid, name, "REMOVE", i,
                    FalconMainVelocity.plugin.isJoinable()));
            Main.gi.debug("Sent player-info packet to FalconCloudServer.");
        }).schedule();
    }

    @Subscribe
    public void onPing(ProxyPingEvent e){
        e.setPing(getPingHandler().handlePing(e.getPing()));
    }


}
