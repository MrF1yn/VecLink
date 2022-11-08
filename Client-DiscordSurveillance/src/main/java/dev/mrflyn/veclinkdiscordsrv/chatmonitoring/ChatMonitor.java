package dev.mrflyn.veclinkdiscordsrv.chatmonitoring;

import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class ChatMonitor {

    public static class ChatData{

        public ChatMonitor chatMonitor;
        public String message;

        public ChatData(ChatMonitor chatMonitor, String message) {
            this.chatMonitor = chatMonitor;
            this.message = message;
        }
    }

    private String guildID;
    private String channelID;

    public ChatMonitor(String guildID, String channelID) {
        this.guildID = guildID;
        this.channelID = channelID;
    }



    public void send(String message){
        Guild guild = VecLinkMainDiscordSRV.jda.getGuildById(guildID);
        if (guild==null)return;
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel==null)return;
        channel.sendMessage(message).queue();
    }
}
