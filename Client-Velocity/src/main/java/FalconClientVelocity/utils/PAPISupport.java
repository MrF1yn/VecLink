package FalconClientVelocity.utils;

import FalconClientVelocity.API.placeholderapinative.PlaceholderExpansion;
import FalconClientVelocity.FalconMainVelocity;
import com.velocitypowered.api.proxy.Player;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.Main;
import org.jetbrains.annotations.NotNull;

public class PAPISupport extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "fcln";
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
                    String cTrue = FalconMainVelocity.plugin.config.getString("placeholder-config.clientStatus.true");
                    String cFalse = FalconMainVelocity.plugin.config.getString("placeholder-config.clientStatus.false");
                    if(Main.client.channel==null||!(Main.client.channel.isActive())){
                        return cFalse;
                    }
                    return cTrue;
            }
        }
        else if(args.length==2){
            switch (args[0]){
                case "playerCount":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return FalconMainVelocity.plugin.server.getPlayerCount()+"";
                    if(!ConnectedFalconClient.CFC.containsKey(args[1])) return "0";
                    return ConnectedFalconClient.CFC.get(args[1]).getOnlinePlayerCount()+"";
                case "onlineStatusBoolean":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return "true";
                    if(!ConnectedFalconClient.CFC.containsKey(args[1])) return "false";
                    return "true";
                case "onlineStatus":
                    String oTrue = FalconMainVelocity.plugin.config.getString("placeholder-config.onlineStatus.true");
                    String oFalse = FalconMainVelocity.plugin.config.getString("placeholder-config.onlineStatus.false");
                    if (args[1].equals(Main.config.getMainConfig().getString("client-id"))) return oTrue;
                    if (!ConnectedFalconClient.CFC.containsKey(args[1])) return oFalse;
                    return oTrue;
            }
        }
        return value;
    }
}
