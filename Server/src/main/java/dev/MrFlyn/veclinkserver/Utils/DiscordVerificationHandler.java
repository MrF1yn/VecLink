package dev.mrflyn.veclinkserver.Utils;

import dev.mrflyn.veclinkserver.Main;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DiscordVerificationHandler {

    private HashMap<String, VerificationData> tokenToData;
    private Timer timer;
    public DiscordVerificationHandler(){
        tokenToData = new HashMap<>();
        timer = new Timer();
    }

    public String submitData(VerificationData data){
        String token = RandomStringUtils.random(6, true, true);
        while (tokenToData.containsKey(token)){
            token = RandomStringUtils.random(6, true, true);
        }
        tokenToData.put(token, data);
        timer.schedule(new VerificationTask(token, tokenToData), 20000);
        return token;
    }

    public VerificationData verify(String token, String userName, String userID){
        if(!tokenToData.containsKey(token))return null;
        VerificationData data = tokenToData.get(token);
        data.userID = userID;
        data.userName = userName;
        tokenToData.remove(token);
        if(Main.db.getPlayerInfoFromUserID(userID)!=null){
            Main.db.deletePlayerInfoFromUserID(userID);
        }
        Main.db.saveUser(userID, userName, UUID.fromString(data.playerUUID), data.playerName);
        return data;
    }




    public static class VerificationData{

        public String playerName;
        public String playerUUID;
        public String userName;
        public String userID;

        public VerificationData(String playerName, String playerUUID) {
            this.playerName = playerName;
            this.playerUUID = playerUUID;
        }

        public boolean equals(VerificationData data){
            return (data.playerName.equals(this.playerName)&&data.playerUUID.equals(this.playerUUID));
        }


    }

    public static class VerificationTask extends TimerTask{

        String token;
        HashMap<String, VerificationData> map;
        public VerificationTask(String token, HashMap<String, VerificationData>map){
            this.token=token;
            this.map = map;
        }
        @Override
        public void run() {
            map.remove(token);
        }
    }
}

