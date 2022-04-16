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
            FalconMainVelocity.plugin.isJoinable())+"\n");
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
                    FalconMainVelocity.plugin.isJoinable()) + "\n");
            Main.gi.debug("Sent player-info packet to FalconCloudServer.");
        }).schedule();
    }

    @Subscribe
    public void onPing(ProxyPingEvent e){
        ServerPing.Builder builder = e.getPing().asBuilder();
        if(FalconMainVelocity.plugin.config.getBoolean("modify-player-count.enabled")){
            //do playercount modification
            String onlineValue = FalconMainVelocity.plugin.config.getString("modify-player-count.online-value");
            String maximumValue = FalconMainVelocity.plugin.config.getString("modify-player-count.maximum-value");
            try {
                builder.onlinePlayers(Integer.parseInt(PlaceholderAPI.setPlaceholdersIfAvailable(null,onlineValue)));
                builder.maximumPlayers(Integer.parseInt(PlaceholderAPI.setPlaceholdersIfAvailable(null,maximumValue)));
            }
            catch (Exception ex){
                if(Main.config.getMainConfig().getBoolean("debug")){
                    ex.printStackTrace();
                }
                FalconMainVelocity.plugin.log("Error trying to parse online-players.");
            }
        }
        if (FalconMainVelocity.plugin.config.getBoolean("modify-motd.enabled")) {
            List<String> valuesList = FalconMainVelocity.plugin.config.getStringList("modify-motd.values");
            if(FalconMainVelocity.plugin.config.getString("modify-motd.display-mode").equalsIgnoreCase("random")) {
                int randomIndex = random.nextInt(valuesList.size());
                builder.description(LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null,valuesList.get(randomIndex))));
            }
            else {
                try {
                    int i = Integer.parseInt(FalconMainVelocity.plugin.config.getString("modify-motd.display-mode"));
                    builder.description(LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null,valuesList.get(i))));
                }catch (Exception ex){
                    if(Main.config.getMainConfig().getBoolean("debug")){
                        ex.printStackTrace();
                    }
                    FalconMainVelocity.plugin.log("Error trying to parse motd.");
                    builder.description(LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null,valuesList.get(0))));
                }
            }
        }
        if (FalconMainVelocity.plugin.config.getBoolean("modify-sampleplayers.enabled")) {
            builder.clearSamplePlayers();
            List<String> valuesList = FalconMainVelocity.plugin.config.getStringList("modify-sampleplayers.values");
            if (FalconMainVelocity.plugin.config.getString("modify-sampleplayers.display-mode").equalsIgnoreCase("random")) {
                int randomIndex = random.nextInt(valuesList.size());
                builder.samplePlayers(new ServerPing.SamplePlayer(
                        LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null,valuesList.get(randomIndex))).content(), UUID.randomUUID()));
            } else if(!FalconMainVelocity.plugin.config.getString("modify-sampleplayers.display-mode").equalsIgnoreCase("all")){
                try {
                    int i = Integer.parseInt(FalconMainVelocity.plugin.config.getString("modify-sampleplayers.display-mode"));
                    builder.samplePlayers(new ServerPing.SamplePlayer(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null,valuesList.get(i))).content(), UUID.randomUUID()));
                } catch (Exception ex) {
                    if(Main.config.getMainConfig().getBoolean("debug")){
                        ex.printStackTrace();
                    }
                    FalconMainVelocity.plugin.log("Error trying to parse motd.");
                    builder.samplePlayers(new ServerPing.SamplePlayer(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null,valuesList.get(0))).content(), UUID.randomUUID()));
                }
            }else {
                for(String s : valuesList){
                    builder.samplePlayers(
                            new ServerPing.SamplePlayer(
                                    PlaceholderAPI.setPlaceholdersIfAvailable(null,s).replace('&', '§'),
                                    UUID.randomUUID()));
                }
            }
        }
        e.setPing(builder.build());
    }


}
