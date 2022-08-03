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

package dev.mrflyn.veclinkspigot.support;
import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getLCS;
import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getMiniMessage;

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
        return VecLinkMainSpigot.plugin.getDescription().getVersion();
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
                    String cTrue = VecLinkMainSpigot.plugin.config.getString("placeholder-config.clientStatus.true");
                    String cFalse = VecLinkMainSpigot.plugin.config.getString("placeholder-config.clientStatus.false");
                    if(Main.client.channel==null||!(Main.client.channel.isActive())){
                        return getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(player, cFalse)));
                    }
                    return getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(player, cTrue)));
            }
        }
        else if(args.length==2){
            switch (args[0]){
                case "playerCount":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return Bukkit.getOnlinePlayers().size()+"";
                    if(!ConnectedVecLinkClient.CFC.containsKey(args[1])) return "0";
                    return ConnectedVecLinkClient.CFC.get(args[1]).getOnlinePlayerCount()+"";
                case "onlineStatusBoolean":
                    if(args[1].equals(Main.config.getMainConfig().getString("client-id")))return "true";
                    if(!ConnectedVecLinkClient.CFC.containsKey(args[1])) return "false";
                    return "true";
                case "onlineStatus":
                    String oTrue = VecLinkMainSpigot.plugin.config.getString("placeholder-config.onlineStatus.true");
                    String oFalse = VecLinkMainSpigot.plugin.config.getString("placeholder-config.onlineStatus.false");
                    if (args[1].equals(Main.config.getMainConfig().getString("client-id")))
                        return getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(player, oTrue)));
                    if (!ConnectedVecLinkClient.CFC.containsKey(args[1]))
                        return getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(player, oFalse)));
                    return getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(player, oTrue)));
            }
        }
        return value;
    }
}
