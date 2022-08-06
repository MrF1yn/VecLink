package dev.mrflyn.veclinkdiscordsrv;




import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import static dev.mrflyn.veclinkdiscordsrv.utils.ExtraUtil.jsonToEmbed;


public class Listeners extends ListenerAdapter {
    Random random = new Random();
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if(event.getGuild()==null)return;
        if (event.getMember()==null)return;
        if(!event.getMember().hasPermission(Permission.ADMINISTRATOR))return;
        switch (event.getName()){
            case "status":
                if (event.getOptions().isEmpty()) {
                    for (ConnectedVecLinkClient client : ConnectedVecLinkClient.CFC.values()) {
                        event.replyEmbeds(getStatusEmbed(client)).queue();
                    }
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

        }

    }

    public MessageEmbed getStatusEmbed(ConnectedVecLinkClient client) {
        
        try {
            String json = JsonParser.parseReader(new FileReader(Main.gi.getConfigLocation()+"status_embed.json")).getAsString();
            json = json.replace("%clientName%", client.getName())
                    .replace("%clientType%", client.getType());
            return (jsonToEmbed( JsonParser.parseString(json).getAsJsonObject() ));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



}
