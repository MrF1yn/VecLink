package FalconClientSpigot.chat;

import FalconClientSpigot.FalconMainSpigot;
import FalconClientSpigot.PacketFormatterSpigot;
import dev.MrFlyn.FalconClient.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class ChatHandler {
    public BukkitTask chatSyncTask;
    public Queue<String> pendingChats;
    public ChatHandler(){
        pendingChats = new PriorityBlockingQueue<>();
    }

    public void startChatSyncTask(){
        if(chatSyncTask!=null)return;
        chatSyncTask =
                Bukkit.getScheduler().runTaskTimerAsynchronously(FalconMainSpigot.plugin, ()->{
                    if(pendingChats.size()>0)
                        Main.client.channel.writeAndFlush(PacketFormatterSpigot.chatPacket(pendingChats.remove()));
                },0L, 10L);
    }

    public void stopChatSyncTask() {
        if(chatSyncTask==null)return;
        chatSyncTask.cancel();
        chatSyncTask = null;
    }
}
