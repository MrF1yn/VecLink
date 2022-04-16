package dev.MrFlyn.FalconServer.Utils;

import dev.MrFlyn.FalconServer.Main;

import java.util.Timer;
import java.util.TimerTask;

public class ConsoleSpamHandler {
    public static ConsoleSpamHandler consoleSpamHandler;
    private int maximumViolations;
    private long resetAfterTime;
    private long lastTime;
    private Integer currentViolation;
    private TimerTask clearTask;
    private boolean isMessageSent;
    private Timer timer;

    private Boolean isSpamming;

    public ConsoleSpamHandler(int violationToTrigger, long resetAfter){
        this.maximumViolations = violationToTrigger;
        this.resetAfterTime = resetAfter;
        isSpamming = false;
        lastTime = System.currentTimeMillis();
        currentViolation = 0;
        isMessageSent = false;
        timer = new Timer("Timer");
        consoleSpamHandler = this;

    }

    public static boolean isSpammingIfEnabled(){
        if (!Main.config.getMainConfig().getBoolean("console-spam-detection.prevent-console-spam")){
            return false;
        }
        return ConsoleSpamHandler.consoleSpamHandler.isSpamming();
    }

    public void onMessageSend(){
//        System.out.println(System.currentTimeMillis()-lastTime);
        if(System.currentTimeMillis()-lastTime<=Main.config.getMainConfig().getLong("console-spam-detection.minimum-time-between-msg")){
            currentViolation++;
        }
        else {
            currentViolation=0;
        }
        if(currentViolation>=maximumViolations){
            if (!isMessageSent) {
                Main.log("Spam detected. Blocking console output...", true);
                isMessageSent = true;
            }
            currentViolation=0;
            isSpamming=true;
            startClearTask();
        }
        lastTime = System.currentTimeMillis();

    }

    public boolean isSpamming(){
        return isSpamming;
    }

    private void startClearTask(){
        if (clearTask!=null)return;
        clearTask = new TimerTask() {

            public void run() {
                if(isSpamming) {
                    if (System.currentTimeMillis() - lastTime >= 5000L) {
                        isSpamming = false;
                        isMessageSent = false;
                        Main.log("Spam detection over. Reinstating console output...", false);

                        currentViolation = 0;
                    }
                }

            }
        };
        long delay = resetAfterTime;
        timer.scheduleAtFixedRate(clearTask, 1L, 1000L);

    }

}
