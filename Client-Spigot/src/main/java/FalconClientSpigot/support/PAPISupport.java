/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package FalconClientSpigot.support;
import FalconClientSpigot.FalconMainSpigot;
import dev.MrFlyn.FalconClient.ClientHandlers.ConnectedFalconClient;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class PAPISupport extends PlaceholderExpansion {

    private static final SimpleDateFormat elapsedFormat = new SimpleDateFormat("HH:mm");


    @Override
    public String getIdentifier() {
        return "fcln";
    }


    @Override
    public String getAuthor() {
        return "MrFlyn";
    }


    @Override
    public String getVersion() {
        return FalconMainSpigot.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
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
                    String cTrue = FalconMainSpigot.plugin.config.getString("placeholder-config.clientStatus.true");
                    String cFalse = FalconMainSpigot.plugin.config.getString("placeholder-config.clientStatus.false");
                    if(Main.client.channel==null||!(Main.client.channel.isActive())){
                        return cFalse;
                    }
                    return cTrue;
            }
        }
        else if(args.length==2){
            switch (args[0]){
                case "playerCount":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return Bukkit.getOnlinePlayers().size()+"";
                    if(!ConnectedFalconClient.CFC.containsKey(args[1])) return "0";
                    return ConnectedFalconClient.CFC.get(args[1]).getOnlinePlayerCount()+"";
                case "onlineStatusBoolean":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return "true";
                    if(!ConnectedFalconClient.CFC.containsKey(args[1])) return "false";
                    return "true";
                case "onlineStatus":
                    String oTrue = FalconMainSpigot.plugin.config.getString("placeholder-config.onlineStatus.true");
                    String oFalse = FalconMainSpigot.plugin.config.getString("placeholder-config.onlineStatus.false");
                    if (args[1].equals(Main.config.getMainConfig().getString("client-id"))) return oTrue;
                    if (!ConnectedFalconClient.CFC.containsKey(args[1])) return oFalse;
                    return oTrue;
            }
        }
        return value;
    }
}
