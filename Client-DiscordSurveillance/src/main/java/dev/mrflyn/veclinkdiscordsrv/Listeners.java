package dev.mrflyn.veclinkdiscordsrv;




import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.ConfigPath;
import dev.mrflyn.veclink.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static dev.mrflyn.veclinkdiscordsrv.utils.ExtraUtil.jsonToEmbed;


public class Listeners extends ListenerAdapter {
    Random random = new Random();
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if(event.getGuild()==null)return;
        if(!event.getGuild().getId().equals(VecLinkMainDiscordSRV.plugin.config.getString("guild_id")))return;
        if (event.getMember()==null)return;
        switch (event.getName()){
            case "status":
                if(!event.getMember().hasPermission(Permission.ADMINISTRATOR))return;
                if(Main.client.channel==null||!(Main.client.channel.isActive())){
                    event.reply("Not connected to VecLink Server. Please try again later.").queue();
                    return;
                }
                if (ConnectedVecLinkClient.CFC.values().isEmpty()) {
                    event.reply("No Clients are connected to the VecLink Server.").queue();
                    return;
                }
                if (event.getOptions().isEmpty()) {
                    List<MessageEmbed> embeds = new ArrayList<>();
                    for (ConnectedVecLinkClient client : ConnectedVecLinkClient.CFC.values()) {
                        embeds.add(getStatusEmbed(client));
                    }
                    event.replyEmbeds(embeds).queue();
                    embeds.clear();
                    return;
                }
                String option = event.getOptions().get(0).getAsString();
                ConnectedVecLinkClient client = ConnectedVecLinkClient.CFC.get(option);

                if (client == null) {
                    event.reply("That client is not connected to the VecLink Server.").queue();
                    return;
                }

                event.replyEmbeds(getStatusEmbed(client)).queue();
                break;
            case "dcverify":
                if(!event.getChannel().getId().equals(VecLinkMainDiscordSRV.plugin.config.getString("verification_channel_id")))return;
                if(Main.client.channel==null||!(Main.client.channel.isActive())){
                    event.reply("Not connected to VecLink Server. Please try again later.").queue();
                    return;
                }
                if(Main.db==null||!Main.db.isConnected()){
                    event.reply("Database offline please try again later.").queue();
                    return;
                }
                String token = event.getOptions().get(0).getAsString();
                event.reply("Processing...").setEphemeral(true).queue();
                Main.client.channel.writeAndFlush(PacketFormatterDiscordSRV.dcVerifyReq(token,event.getMember().getId(), event.getMember().getUser().getAsTag()));
        }

    }

    public MessageEmbed getStatusEmbed(ConnectedVecLinkClient client) {
        
        try {
            String json = JsonParser.parseReader(new FileReader(Main.gi.getConfigLocation()+"status_embed.json")).toString();
            json = json.replace("%clientName%", client.getName())
                    .replace("%clientType%", client.getType())
                    .replace("%clientTps%", client.getTps1min()+", "+client.getTps5min()+", "+client.getTps15min())
                    .replace("%clientMspt%", client.getMspt()+"")
                    .replace("%clientGroups%", client.getGroups().toString())
                    .replace("%clientBackendServers%", client.getBackendServers().toString())
                    .replace("%clientCpuCores%", client.getCpuCores()+"")
                    .replace("%clientCpuUsage%", client.getCpuUsagePercent()+"%")
                    .replace("%clientMemUsage%", client.getCurrentMemoryUsage()+"/"+client.getMaxMemory()+" ("+client.getMemoryUsagePercent()+"%"+")")
                    .replace("%clientOS%", client.getOsName())
                    .replace("%clientJoinable%", client.isCanJoin()+"");
            return (jsonToEmbed( JsonParser.parseString(json).getAsJsonObject()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



}
