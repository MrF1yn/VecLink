package dev.mrflyn.veclinkdiscordsrv.commands;

import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkdiscordsrv.VecLinkMainDiscordSRV;
import dev.mrflyn.veclinkdiscordsrv.commands.handler.SubCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RemRole implements SubCommand {
    @Override
    public boolean onSubCommand(String sender, String cmd, String[] args) {
        // /remRole <roleID> <userID>
        if(args.length<2)return true;
        String roleID = args[0];
        String userID = args[1];

        Guild guild = VecLinkMainDiscordSRV.jda.getGuildById(VecLinkMainDiscordSRV.plugin.config.getString("guild_id"));
        if (guild==null){
            Main.gi.log("The Guild doesn't exists.");
            return true;
        }
        Role role = guild.getRoleById(roleID);
        if (role==null){
            Main.gi.log("The role doesn't exists.");
            return true;
        }
        Member user = guild.getMemberById(userID);
        if (user==null){
            Main.gi.log("The user doesn't exists in the guild.");
            return true;
        }
        if(!user.getRoles().contains(role)){
            Main.gi.log("The user doesn't have the role.");
            return true;
        }
        guild.removeRoleFromMember(user, role).queue((s)->{
            Main.gi.log("Removed role: "+role.getName()+" to "+ user.getEffectiveName());
        }, Throwable::printStackTrace);
        return true;
    }

    @Override
    public String getName() {
        return "remRole";
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
