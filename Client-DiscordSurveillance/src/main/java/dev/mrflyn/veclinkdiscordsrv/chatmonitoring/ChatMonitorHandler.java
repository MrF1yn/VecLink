package dev.mrflyn.veclinkdiscordsrv.chatmonitoring;

import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkdiscordsrv.PacketFormatterDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.livestatus.LiveStatus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class ChatMonitorHandler {
    private ConcurrentHashMap<String, ChatMonitor> registeredChatMonitors;
    private String INCOMING_FORMAT;
    private String OUTGOING_FORMAT;
    private BlockingQueue<ChatMonitor.ChatData> chatQueue;

    public ChatMonitorHandler(){
        registeredChatMonitors = new ConcurrentHashMap<>();
        chatQueue = new LinkedBlockingDeque<>();
    }

    public void init(){
        INCOMING_FORMAT = VecLinkMainDiscordSRV.plugin.config.getString("chat_monitor.incoming_format");
        OUTGOING_FORMAT = VecLinkMainDiscordSRV.plugin.config.getString("chat_monitor.outgoing_format");

        Main.client.channel.writeAndFlush(PacketFormatterDiscordSRV.chatMonitorInit(INCOMING_FORMAT));

        for(String s : VecLinkMainDiscordSRV.plugin.config.getConfigurationSection("chat_monitor.registered").getKeys(false)){
            Main.gi.log(s);
            String[] raw = s.split(":");
            String guildID = raw[0];
            String channelID = raw[1];
            String clientName = VecLinkMainDiscordSRV.plugin.config.getString("chat_monitor.registered."+s+".client_name");
            boolean chatInput = VecLinkMainDiscordSRV.plugin.config.getBoolean("chat_monitor.registered."+s+".chat_input");
            registeredChatMonitors.put(clientName, new ChatMonitor(guildID,channelID));

        }

        new Timer("CHAT_MONITOR").scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run(){

                if (chatQueue.isEmpty())return;
                try {
                    ChatMonitor.ChatData data = chatQueue.take();
                    data.chatMonitor.send(data.message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        },0L, 500L);
    }
    
    public void onChatMonitorPacket(ConnectedVecLinkClient client, String message){
        if (client==null)return;
        ChatMonitor monitor = registeredChatMonitors.get(client.getName());
        if (monitor==null)return;
        chatQueue.add(new ChatMonitor.ChatData(monitor,
                message
        ));
    }
}
