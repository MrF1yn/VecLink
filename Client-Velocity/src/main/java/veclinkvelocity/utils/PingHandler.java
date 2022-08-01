package veclinkvelocity.utils;

import veclinkvelocity.API.placeholderapinative.PlaceholderAPI;
import veclinkvelocity.VecLinkMainVelocity;
import com.velocitypowered.api.proxy.server.ServerPing;
import dev.MrFlyn.veclink.Main;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static veclinkvelocity.VecLinkMainVelocity.getLCS;
import static veclinkvelocity.VecLinkMainVelocity.getMiniMessage;

public class PingHandler {
    Random random;
    boolean modifyPlayerCount;
    boolean modifyMOTD;
    boolean modifySP;
    //-1 = random
    //-2 = all
    int displayModeMOTD;
    int displayModeSP;
    String playerCount;
    String maximumPlayerCount;
    List<String> motdList;
    List<String> spList;

    public PingHandler(){

        this.random = new Random();
        this.modifyPlayerCount = VecLinkMainVelocity.plugin.config.getBoolean("modify-player-count.enabled");
        this.modifyMOTD = VecLinkMainVelocity.plugin.config.getBoolean("modify-motd.enabled");
        this.modifySP = VecLinkMainVelocity.plugin.config.getBoolean("modify-sampleplayers.enabled");
        this.displayModeMOTD = VecLinkMainVelocity.plugin.config.getString("modify-motd.display-mode").equalsIgnoreCase("random")?-1:
                Integer.parseInt(VecLinkMainVelocity.plugin.config.getString("modify-motd.display-mode"));
        this.displayModeSP = VecLinkMainVelocity.plugin.config.getString("modify-sampleplayers.display-mode").equalsIgnoreCase("random") ? -1 :
                VecLinkMainVelocity.plugin.config.getString("modify-sampleplayers.display-mode").equalsIgnoreCase("all")?-2:
                Integer.parseInt(VecLinkMainVelocity.plugin.config.getString("modify-sampleplayers.display-mode"));

        this.motdList = VecLinkMainVelocity.plugin.config.getStringList("modify-motd.values");
        this.spList = VecLinkMainVelocity.plugin.config.getStringList("modify-sampleplayers.values");
        this.playerCount = VecLinkMainVelocity.plugin.config.getString("modify-player-count.online-value");
        this.maximumPlayerCount = VecLinkMainVelocity.plugin.config.getString("modify-player-count.maximum-value");



    }

    public ServerPing handlePing(ServerPing serverPing){
        ServerPing.Builder builder = serverPing.asBuilder();

        if(this.modifyPlayerCount){
            try {
                builder.onlinePlayers(Integer.parseInt(PlaceholderAPI.setPlaceholdersIfAvailable(null,playerCount)));
                builder.maximumPlayers(Integer.parseInt(PlaceholderAPI.setPlaceholdersIfAvailable(null,maximumPlayerCount)));
            }
            catch (Exception ex){
                if(Main.config.getMainConfig().getBoolean("debug")){
                    ex.printStackTrace();
                }
                VecLinkMainVelocity.plugin.log("Error trying to parse online-players.");
            }
        }
        if(this.modifyMOTD){
            if(this.displayModeMOTD==-1) {
                int randomIndex = random.nextInt(this.motdList.size());
                builder.description(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, this.motdList.get(randomIndex))));
            }
            else {
                try {
                    builder.description(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, this.motdList.get(this.displayModeMOTD))));
                }catch (Exception ex){
                    if(Main.config.getMainConfig().getBoolean("debug")){
                        ex.printStackTrace();
                    }
                    VecLinkMainVelocity.plugin.log("Error trying to parse motd.");
                    builder.description(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, this.motdList.get(0))));
                }
            }
        }
        if(this.modifySP){
            builder.clearSamplePlayers();
            if (this.displayModeSP==-1) {

                int randomIndex = random.nextInt(this.spList.size());

                builder.samplePlayers(
                        new ServerPing.SamplePlayer(
                                getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, this.spList.get(randomIndex)))),
                                UUID.randomUUID()));
            }
            else if(this.displayModeSP==-2){
                for(String s : this.spList){
                    builder.samplePlayers(
                            new ServerPing.SamplePlayer(
                                    getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, s))),
                                    UUID.randomUUID()));
                }
            }
            else{
                try {
                    builder.samplePlayers(new ServerPing.SamplePlayer(
                            getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, this.spList.get(this.displayModeSP)))),
                            UUID.randomUUID()));
                } catch (Exception ex) {
                    if(Main.config.getMainConfig().getBoolean("debug")){
                        ex.printStackTrace();
                    }
                    VecLinkMainVelocity.plugin.log("Error trying to parse motd.");
                    builder.samplePlayers(new ServerPing.SamplePlayer(
                            getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, this.spList.get(0)))),
                            UUID.randomUUID()));
                }
            }
        }





        return builder.build();
    }


}
