package dev.mrflyn.veclinkdiscordsrv.livestatus;

import dev.mrflyn.veclink.ClientHandlers.ClientHandler;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.ClientHandlers.VecLinkClient;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.utils.ExtraUtil;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LiveStatus {

    private String guildID;
    private String channelID;
    private String clientName;
    private String statusEmbed;
    private String offlineEmbed;
    private String embedID;

    public LiveStatus(String guildID, String channelID, String clientName, String statusEmbed, String offlineEmbed) {
        this.guildID = guildID;
        this.channelID = channelID;
        this.clientName = clientName;
        this.statusEmbed = statusEmbed;
        this.offlineEmbed = offlineEmbed;
    }

    public void update(){
//        CompletableFuture.runAsync(()->{
        ConnectedVecLinkClient client = ConnectedVecLinkClient.CFC.get(clientName);

        Guild guild = VecLinkMainDiscordSRV.jda.getGuildById(guildID);
        if (guild==null)return;
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel==null)return;
        if (embedID == null){
            MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
            List<Message> mess = history.getRetrievedHistory();
            for(Message m: mess){
                m.delete().queue();
            }
            Main.gi.log("UPDATE CALLED 1");
            MessageEmbed embed = ExtraUtil.getStatusEmbed(null, offlineEmbed);
            Main.gi.log(embed.toString() + "TEST 6");
            channel.sendMessageEmbeds(embed).queue((message) -> {
                embedID = message.getId();
                Main.gi.log("EMBED SENT");
                processEmbed(client, channel);
            });
            Main.gi.log("UPDATE CALLED 2");
            return;
        }
            processEmbed(client, channel);

//        });
    }

    public void processEmbed(ConnectedVecLinkClient client, TextChannel channel){
        if (client==null){
            channel.editMessageEmbedsById(embedID, ExtraUtil.getStatusEmbed(null, offlineEmbed)).queue();
            return;
        }
        if(!ConnectedVecLinkClient.CFC.containsKey(client.getName())){
            channel.editMessageEmbedsById(embedID, ExtraUtil.getStatusEmbed(null, offlineEmbed)).queue();
            return;
        }
        channel.editMessageEmbedsById(embedID, ExtraUtil.getStatusEmbed(client, statusEmbed)!=null?ExtraUtil.getStatusEmbed(client, statusEmbed):
                ExtraUtil.getStatusEmbed(null, offlineEmbed)).queue();
    }



}
