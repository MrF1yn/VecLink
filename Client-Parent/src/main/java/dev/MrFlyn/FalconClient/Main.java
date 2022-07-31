package dev.MrFlyn.FalconClient;



import dev.MrFlyn.FalconClient.ClientHandlers.PacketHandler;
import dev.MrFlyn.FalconClient.ClientHandlers.FalconClient;
import org.simpleyaml.configuration.file.YamlFile;



public class Main implements GlobalInterface{

    public static MainConfig config;
    public static FalconClient client;
    public static GlobalInterface gi;
    public static PacketHandler pi;

    public static void main(String[] args){
        gi = new Main();
        pi = new PacketHandlerStandalone();
        enable();
    }

    public static void enable(){
        config = new MainConfig(new YamlFile(gi.getConfigLocation()+"falconClient.yml"), new YamlFile(gi.getConfigLocation()+"messages.yml"));
        config.init();
        String ip = config.getMainConfig().getString("host").split(":")[0];
        int port = Integer.parseInt(config.getMainConfig().getString("host").split(":")[1]);
        client = new FalconClient(ip, port);
        client.start();
    }

    @Override
    public <T> void log(T message) {
        System.out.println("[FalconCloudClient] "+message.toString());
    }

    @Override
    public <T> void debug(T message) {

        if (config.getMainConfig().getBoolean("debug"))
            System.out.println("[FalconCloudClient][Debug] "+message.toString());

    }


    @Override
    public String getServerType() {
        return "TEST";
    }

    @Override
    public String getConfigLocation() {
        return "";
    }

    @Override
    public void startKeepAliveTask() {

    }

    @Override
    public void stopKeepAliveTask() {

    }
}
