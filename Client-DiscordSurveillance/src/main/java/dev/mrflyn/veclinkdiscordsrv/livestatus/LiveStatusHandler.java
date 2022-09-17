package dev.mrflyn.veclinkdiscordsrv.livestatus;

import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclink.api.Monitor;
import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.utils.ExtraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LiveStatusHandler {

    private ConcurrentHashMap<String,LiveStatus> registeredStatuses;

    public LiveStatusHandler(){
        registeredStatuses = new ConcurrentHashMap<>();
    }

    public void init(){
        Main.client.registerMonitors((name)->{
            Main.gi.log("MONITOR");
            if (!registeredStatuses.containsKey(name))return;
            Main.gi.log("MONITOR CALLED");
            registeredStatuses.get(name).update();
        });
        for(String s : VecLinkMainDiscordSRV.plugin.config.getConfigurationSection("live_status.registered").getKeys(false)){
            Main.gi.log(s);
            String[] raw = s.split(":");
            String guildID = raw[0];
            String channelID = raw[1];
            String clientName = VecLinkMainDiscordSRV.plugin.config.getString("live_status.registered."+s+".client_name");
            String statusEmbed = VecLinkMainDiscordSRV.plugin.config.getString("live_status.registered."+s+".status_embed");
            String offlineEmbed = VecLinkMainDiscordSRV.plugin.config.getString("live_status.registered."+s+".offline_embed");
            registeredStatuses.put(clientName, new LiveStatus(guildID,channelID,clientName,statusEmbed,offlineEmbed));
            registeredStatuses.get(clientName).update();
        }
    }


}
