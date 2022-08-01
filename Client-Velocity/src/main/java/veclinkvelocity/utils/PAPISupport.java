package veclinkvelocity.utils;

import veclinkvelocity.API.placeholderapinative.PlaceholderAPI;
import veclinkvelocity.API.placeholderapinative.PlaceholderExpansion;
import veclinkvelocity.VecLinkMainVelocity;
import com.velocitypowered.api.proxy.Player;
import dev.MrFlyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.MrFlyn.veclink.Main;
import org.jetbrains.annotations.NotNull;

import static veclinkvelocity.VecLinkMainVelocity.getLCS;
import static veclinkvelocity.VecLinkMainVelocity.getMiniMessage;

public class PAPISupport extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "vcl";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MrFlyn";
    }

    @Override
    public String onPlaceholderRequest(final Player player, final @NotNull String params) {
        String value="N/A";
        String[] args = params.split("_");
        if(args.length==1){
            switch (args[0]){
                case "clientStatusBoolean":
                    if(Main.client.channel==null||!(Main.client.channel.isActive())){
                        return "false";
                    }
                    return "true";
                case "clientStatus":
                    String cTrue = VecLinkMainVelocity.plugin.config.getString("placeholder-config.clientStatus.true");
                    String cFalse = VecLinkMainVelocity.plugin.config.getString("placeholder-config.clientStatus.false");
                    if(Main.client.channel==null||!(Main.client.channel.isActive())){
                        return getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, cFalse)));
                    }
                    return getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, cTrue)));
            }
        }
        else if(args.length==2){
            switch (args[0]){
                case "playerCount":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return VecLinkMainVelocity.plugin.server.getPlayerCount()+"";
                    if(!ConnectedVecLinkClient.CFC.containsKey(args[1])) return "0";
                    return ConnectedVecLinkClient.CFC.get(args[1]).getOnlinePlayerCount()+"";
                case "onlineStatusBoolean":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return "true";
                    if(!ConnectedVecLinkClient.CFC.containsKey(args[1])) return "false";
                    return "true";
                case "onlineStatus":
                    String oTrue = VecLinkMainVelocity.plugin.config.getString("placeholder-config.onlineStatus.true");
                    String oFalse = VecLinkMainVelocity.plugin.config.getString("placeholder-config.onlineStatus.false");
                    if (args[1].equals(Main.config.getMainConfig().getString("client-id")))
                        return getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, oTrue)));
                    if (!ConnectedVecLinkClient.CFC.containsKey(args[1]))
                        getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, oFalse)));
                    return getLCS().serialize(getMiniMessage().deserialize(PlaceholderAPI.setPlaceholdersIfAvailable(null, oTrue)));
            }
        }
        return value;
    }
}
