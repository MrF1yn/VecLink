package dev.mrflyn.veclinkserver.ServerHandlers;

import io.netty.channel.Channel;

import java.util.TimerTask;


public class DeAuthTask extends TimerTask {
    private Channel c;
    public DeAuthTask(Channel c){
        this.c = c;
    }


    public void run() {
        if(ServerHandler.AuthorisedClients.contains(c))return;
        if(c.isOpen())
            c.close();
    }

}
