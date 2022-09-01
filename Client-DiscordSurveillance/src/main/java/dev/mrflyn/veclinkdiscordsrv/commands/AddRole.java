package dev.mrflyn.veclinkdiscordsrv.commands;

import dev.mrflyn.veclinkdiscordsrv.commands.handler.SubCommand;

public class AddRole implements SubCommand {
    @Override
    public boolean onSubCommand(String sender, String cmd, String[] args) {
        return false;
    }

    @Override
    public String getName() {
        return null;
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
