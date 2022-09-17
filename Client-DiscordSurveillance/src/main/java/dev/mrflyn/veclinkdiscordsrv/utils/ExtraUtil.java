package dev.mrflyn.veclinkdiscordsrv.utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.mrflyn.veclink.ClientHandlers.ConnectedVecLinkClient;
import dev.mrflyn.veclink.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.Validate;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;

public class ExtraUtil {
    public static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<String> originals, T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Validate.notNull(originals, "Originals cannot be null");
        Iterator var4 = originals.iterator();

        while(var4.hasNext()) {
            String string = (String)var4.next();
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }


    public static boolean startsWithIgnoreCase(String string, String prefix) throws IllegalArgumentException, NullPointerException {
        Validate.notNull(string, "Cannot check a null string for a match");
        return string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static MessageEmbed jsonToEmbed(JsonObject json){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive titleObj = json.getAsJsonPrimitive("title");
        if (titleObj != null){ // Make sure the object is not null before adding it onto the embed.
            embedBuilder.setTitle(titleObj.getAsString());
        }

        JsonObject authorObj = json.getAsJsonObject("author");
        if (authorObj != null) {
            String authorName = authorObj.get("name").getAsString();
            String authorIconUrl = authorObj.get("icon_url").getAsString();
            if (authorIconUrl != null) // Make sure the icon_url is not null before adding it onto the embed. If its null then add just the author's name.
                embedBuilder.setAuthor(authorName, authorIconUrl);
            else
                embedBuilder.setAuthor(authorName);
        }

        JsonPrimitive descObj = json.getAsJsonPrimitive("description");
        if (descObj != null){
            embedBuilder.setDescription(descObj.getAsString());
        }

        JsonPrimitive colorObj = json.getAsJsonPrimitive("color");
        if (colorObj != null){
            Color color = new Color(colorObj.getAsInt());
            embedBuilder.setColor(color);
        }

        JsonArray fieldsArray = json.getAsJsonArray("fields");
        if (fieldsArray != null) {
            // Loop over the fields array and add each one by order to the embed.
            fieldsArray.forEach(ele -> {
                String name = ele.getAsJsonObject().get("name").getAsString();
                String content = ele.getAsJsonObject().get("value").getAsString();
                boolean inline = ele.getAsJsonObject().get("inline").getAsBoolean();
                embedBuilder.addField(name, content, inline);
            });
        }

        JsonObject thumbnailObj = json.getAsJsonObject("thumbnail");
        if (thumbnailObj != null){
            embedBuilder.setThumbnail(thumbnailObj.get("url").getAsString());
        }
        JsonObject imageObj = json.getAsJsonObject("image");
        if (imageObj != null) {
            embedBuilder.setImage(imageObj.get("url").getAsString());
        }

        JsonObject footerObj = json.getAsJsonObject("footer");
        if (footerObj != null){
            String content = footerObj.get("text").getAsString();
            String footerIconUrl = footerObj.get("icon_url").getAsString();

            if (footerIconUrl != null)
                embedBuilder.setFooter(content, footerIconUrl);
            else
                embedBuilder.setFooter(content);
        }

        return embedBuilder.build();
    }

    public static MessageEmbed getStatusEmbed(ConnectedVecLinkClient client, String embedFile) {

        try {
            String json = JsonParser.parseReader(new FileReader(Main.gi.getConfigLocation()+embedFile)).toString();
            if (client==null)return (jsonToEmbed( JsonParser.parseString(json).getAsJsonObject()));

            json = json.replace("%clientName%", client.getName())
                    .replace("%clientType%", client.getType())
                    .replace("%clientTps%", client.getTps1min()+", "+client.getTps5min()+", "+client.getTps15min())
                    .replace("%clientMspt%", client.getMspt()+"")
                    .replace("%totalPlayers%", client.getOnlinePlayerCount()+"")
                    .replace("%clientGroups%", client.getGroups().toString())
                    .replace("%clientBackendServers%", client.getBackendServers().toString())
                    .replace("%clientCpuCores%", client.getCpuCores()+"")
                    .replace("%clientCpuUsage%", client.getCpuUsagePercent()+"%")
                    .replace("%clientMemUsage%", client.getCurrentMemoryUsage()+"/"+client.getMaxMemory()+" ("+client.getMemoryUsagePercent()+"%"+")")
                    .replace("%clientOS%", client.getOsName())
                    .replace("%clientJoinable%", client.isCanJoin()+"");
            return (jsonToEmbed( JsonParser.parseString(json).getAsJsonObject()));
        } catch (Exception e) {
            if (e instanceof NullPointerException){
                return null;
            }
            e.printStackTrace();
        }
        return null;
    }
}
