package dev.MrFlyn.FalconServer;

public class ShutDownHook extends Thread{

    @Override
    public void run()
    {
        System.out.println("FalconCloud is terminated.");
    }
}
