package dev.mrflyn.veclinkspigot.chat;

import dev.mrflyn.veclinkspigot.VecLinkMainSpigot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getLCS;
import static dev.mrflyn.veclinkspigot.VecLinkMainSpigot.getMiniMessage;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){

        if(!VecLinkMainSpigot.plugin.config.getBoolean("chat-module.enabled"))return;
        Player p = e.getPlayer();
        if(VecLinkMainSpigot.plugin.config.getString("chat-module.regular-format")!=null&&
                !VecLinkMainSpigot.plugin.config.getString("chat-module.regular-format").equals("")){
            String format = getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(p,
                            VecLinkMainSpigot.plugin.config.getString("chat-module.regular-format")))).replace("[message]","%2$s")
                    .replace("[player]","%1$s");
            e.setFormat(format);
        }
        if(VecLinkMainSpigot.plugin.config.getBoolean("chat-module.sync-chats")){
            String format = getLCS().serialize(getMiniMessage().deserialize(VecLinkMainSpigot.PAPIparseIfAvailable(p,
                            VecLinkMainSpigot.plugin.config.getString("chat-module.outgoing-format"))))
                    .replace("[message]",e.getMessage()).replace("[player]",p.getName());
            VecLinkMainSpigot.plugin.chatHandler.pendingChats.add(format);
        }
    }
}
