package dev.mrflyn.veclinkdiscordsrv.commands;

import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.commands.handler.SubCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class sendMsg implements SubCommand {
    @Override
    public boolean onSubCommand(String sender, String cmd, String[] args) {
        // /sendMsg <guildID>:<channelID> %clientName% Successfully Connected to VecLinkServer.
        if(args.length<2)return true;
        String[] ids = args[0].split(":");
        Guild guild = VecLinkMainDiscordSRV.jda.getGuildById(ids[0]);
        if (guild==null)return true;
        TextChannel channel = guild.getTextChannelById(ids[1]);
        if(channel==null)return true;
        String msg="";
        for(int i = 1; i<args.length; i++){
            msg = msg + args[i] + " ";
        }
        msg = msg.trim();
        channel.sendMessage(msg).queue();
        return true;
    }

    @Override
    public String getName() {
        return "sendMsg";
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }
}
