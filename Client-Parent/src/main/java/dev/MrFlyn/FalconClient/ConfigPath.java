package dev.MrFlyn.FalconClient;

public enum ConfigPath {

    CORRECT_FORMAT_REMOTE_CMD("CORRECT_FORMAT_REMOTE_CMD", "§7[§bFalcon§fCloud§7]§f➣ §cThe correct format is: falcon command [serverName|group:] [@c(console)|playerName] [command]."),
    CLIENT_NOT_CONNECTED("CLIENT_NOT_CONNECTED", "§7[§bFalcon§fCloud§7]§f➣ §cThe Client is not connected to the FalconCloud Server."),
    CORRECT_FORMAT_CHATGROUP("CORRECT_FORMAT_CHATGROUP", "§7[§bFalcon§fCloud§7]§f➣ §cThe correct format is: falcon chatGroup <groupName> <message>."),
    CORRECT_FORMAT_MUTECHAT("CORRECT_FORMAT_MUTECHAT", "§7[§bFalcon§fCloud§7]§f➣ §cThe correct format is: falcon muteChat <groupName>."),
    CHATGROUP_NOT_EXISTS("CHATGROUP_NOT_EXISTS", "§7[§bFalcon§fCloud§7]§f➣ §cThe mentioned chat-group does not exists."),
    REMOTE_CMD_SENT("REMOTE_CMD_SENT", "§7[§bFalcon§fCloud§7]§f➣ §aRemote Command sent successfully."),
    DISCONNECTED("DISCONNECT_MSG", "§7[§bFalcon§fCloud§7]§f➣ §aCommunication Dropped..."),
    RECONNECTED("RECONNECTED_MSG", "§7[§bFalcon§fCloud§7]§f➣ §aTrying to reconnect..."),
    ERROR("ERROR_MSG", "§7[§bFalcon§fCloud§7]§f➣ §cError Occurred!"),
    ALREADY_CONNECTED("ALREADY_CONNECTED_MSG", "§7[§bFalcon§fCloud§7]§f➣ §cAlready Connected to FalconCloud Server."),
    NO_PERM("NO_PERM_MSG", "§7[§bFalcon§fCloud§7]§f➣ §cYou don't have the permission to execute the command."),
    CHAT_GRP_MUTED("CHAT_GRP_MUTED", "§7[§bFalcon§fCloud§7]§f➣ §aChat group muted successfully."),
    CHAT_GRP_UNMUTED("CHAT_GRP_UNMUTED", "§7[§bFalcon§fCloud§7]§f➣ §aChat group un-muted successfully."),
    RELOAD_RESPONSE("RELOAD_RESPONSE", "§7[§bFalcon§fCloud§7]§f➣ §aReloading..."),
    NOT_A_PLAYER("NOT_A_PLAYER", "§7[§bFalcon§fCloud§7]§f➣ §aOnly players can execute this command.");


    private String path;
    private String value;
    private ConfigPath(String path, String defaultValue){
        this.path = path;
        this.value = defaultValue;
    }

    @Override
    public String toString(){
        return path;
    }

    public String getValue() {
        return value;
    }
}
