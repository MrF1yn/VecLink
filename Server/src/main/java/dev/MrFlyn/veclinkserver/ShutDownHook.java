package dev.MrFlyn.veclinkserver;

public class ShutDownHook extends Thread{

    @Override
    public void run()
    {
        System.out.println("VecLink is terminated.");
    }
}
