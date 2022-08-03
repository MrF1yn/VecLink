package dev.mrflyn.veclinkserver;

public class ShutDownHook extends Thread{

    @Override
    public void run()
    {
        System.out.println("VecLink is terminated.");
    }
}
