package dev.mrflyn.veclinkdiscordsrv.commands;

import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.commands.handler.SubCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class changeNick implements SubCommand {
    @Override
    public boolean onSubCommand(String sender, String cmd, String[] args) {
        // /changeNick <userID> <name>
        if(args.length<2)return true;
        String userID = args[0];
        String rawName="";
        for(int i = 1; i<args.length; i++){
            rawName = rawName + args[i] + " ";
        }
        rawName = rawName.trim();
        String name = rawName;

        Guild guild = VecLinkMainDiscordSRV.jda.getGuildById(VecLinkMainDiscordSRV.plugin.config.getString("guild_id"));
        if (guild==null){
            Main.gi.log("The Guild doesn't exists.");
            return true;
        }
        Member user = guild.getMemberById(userID);
        if (user==null){
            Main.gi.log("The user doesn't exists in the guild.");
            return true;
        }
        guild.modifyNickname(user,name).queue((s)->{
            Main.gi.log("Changed NickName of user: "+user.getUser().getAsTag()+" to "+name+".");
        }, Throwable::printStackTrace);
        return true;
    }

    @Override
    public String getName() {
        return "changeNick";
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
