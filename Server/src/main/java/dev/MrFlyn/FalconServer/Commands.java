package dev.MrFlyn.FalconServer;

import dev.MrFlyn.FalconServer.ServerHandlers.FalconClient;
import dev.MrFlyn.FalconServer.ServerHandlers.ServerHandler;

import java.util.Scanner;

public class Commands extends Thread{

    @Override
    public void run() {

        Scanner sc = new Scanner(System.in);
        while (true){
            if(sc.hasNextLine()){
                String input = sc.nextLine();

                if(input.split(" ")[0].equalsIgnoreCase("falcon")){
                    if(input.split(" ").length<2)continue;
                    switch (input.split(" ")[1]){
                        case "stop":
                            Main.log("Stopping FalconCloud Server...", true);
                            System.exit(0);
                            break;
                        case "status":
                            Main.log("Showing current status of all connected clients:-", true);
                            for(FalconClient c :ServerHandler.ClientsByName.values()){
                                Main.log(" ", true);
                                for(String s: c.getFormattedClientInfo()){
                                    Main.log(s, true);
                                }
                            }
                            break;
                        case "player-list":
                            if(input.split(" ").length<3){
                                Main.log("Please mention the client-name", true);
                                continue;
                            }
                            String cName = input.split(" ")[2];
                            if(!ServerHandler.ClientsByName.containsKey(cName)){
                                Main.log("That client doesn't exists or its not connected.", true);
                                continue;
                            }
                            FalconClient cl = ServerHandler.ClientsByName.get(cName);
                            Main.log("Name: "+cName+", Total-Players: "+cl.getOnlinePlayerCount(), true);
                            String names = "";
                            for(String name : cl.playersByName.keySet()) {
                                names = name+",";
                            }
                            if(cl.playersByName.keySet().size()>0)
                                Main.log(names.substring(0, names.length()-1), true);
                            break;
                    }
                }else {
                    switch (input.split(" ")[0]) {
                        case "stop":
                            Main.log("Stopping FalconCloud Server...", true);
                            System.exit(0);
                            break;
                        case "status":
                            Main.log("Showing current status of all connected clients:-", true);
                            for (FalconClient c : ServerHandler.ClientsByName.values()) {
                                Main.log(" ", true);
                                for (String s : c.getFormattedClientInfo()) {
                                    Main.log(s, true);
                                }
                            }
                            break;
                        case "player-list":
                            if (input.split(" ").length < 2) {
                                Main.log("Please mention the client-name", true);
                                continue;
                            }
                            String cName = input.split(" ")[1];
                            if (!ServerHandler.ClientsByName.containsKey(cName)) {
                                Main.log("That client doesn't exists or its not connected.", true);
                                continue;
                            }
                            FalconClient cl = ServerHandler.ClientsByName.get(cName);
                            Main.log("Name: " + cName + ", Total-Players: " + cl.getOnlinePlayerCount(), true);
                            String names = "";
                            for (String name : cl.playersByName.keySet()) {
                                names = name + ",";
                            }
                            if(cl.playersByName.keySet().size()>0)
                                Main.log(names.substring(0, names.length() - 1), true);
                            break;
                    }
                }
            }
        }


    }

}
