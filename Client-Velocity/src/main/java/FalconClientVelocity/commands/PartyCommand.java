package FalconClientVelocity.commands;


import FalconClientVelocity.PacketFormatterVelocity;
import FalconClientVelocity.commands.handler.SubCommand;
import FalconClientVelocity.parties.Party;
import FalconClientVelocity.utils.ExtraUtil;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.MrFlyn.FalconClient.ConfigPath;
import dev.MrFlyn.FalconClient.Main;

import java.util.List;

public class PartyCommand implements SubCommand {

    public PartyCommand(){

    }

    @Override
    public boolean onSubCommand(CommandSource sender, String[] args) {
        if(Main.client.channel==null||!(Main.client.channel.isActive())){
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.CLIENT_NOT_CONNECTED.toString())));
            return true;
        }
        if(!(sender instanceof Player)){
            sender.sendMessage(ExtraUtil.color(Main.config.getLanguageConfig().getString(ConfigPath.NOT_A_PLAYER.toString())));
            return true;
        }
        Player p = (Player) sender;
        if(args.length==0)return true;
        switch (args[0]){
            case "invite":
                if(args.length<2){
                    return true;
                }
                //do checks
                if(Party.isPlayerInParty(p)&&!Party.getAllParties().containsKey(p.getUniqueId())){
                    // player is in a party, but he is not an owner.
                    p.sendMessage(ExtraUtil.color("&cYou need to be a party owner to invite people."));
                    return true;
                }
                //send invite packets to falcon server.
                Main.client.channel.writeAndFlush(PacketFormatterVelocity.partyInvite(p.getUsername(), args[1]));

                break;
        }
        return true;
    }

    @Override
    public List<String> suggestTabCompletes(CommandSource sender, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "party";
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public String getPermission() {
        return "falcon.command.party";
    }
}
