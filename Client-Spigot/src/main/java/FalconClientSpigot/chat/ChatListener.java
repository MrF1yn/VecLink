package FalconClientSpigot.chat;

import FalconClientSpigot.FalconMainSpigot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        if(!FalconMainSpigot.plugin.config.getBoolean("chat-module.enabled"))return;
        Player p = e.getPlayer();
        if(FalconMainSpigot.plugin.config.getString("chat-module.regular-format")!=null&&
                !FalconMainSpigot.plugin.config.getString("chat-module.regular-format").equals("")){
            String format = ChatColor.translateAlternateColorCodes('&',FalconMainSpigot.PAPIparseIfAvailable(p,
                            FalconMainSpigot.plugin.config.getString("chat-module.regular-format"))).replace("[message]","%2$s")
                    .replace("[player]","%1$s");
            e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
        }
        if(FalconMainSpigot.plugin.config.getBoolean("chat-module.sync-chats")){
            String format = ChatColor.translateAlternateColorCodes('&',
                            FalconMainSpigot.PAPIparseIfAvailable(p,FalconMainSpigot.plugin.config.getString("chat-module.outgoing-format")))
                    .replace("[message]",e.getMessage()).replace("[player]",p.getName());
            FalconMainSpigot.plugin.chatHandler.pendingChats.add(format);
        }
    }
}
