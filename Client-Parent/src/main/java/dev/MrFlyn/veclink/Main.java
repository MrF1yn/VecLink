package dev.mrflyn.veclink;



import dev.mrflyn.veclink.ClientHandlers.PacketHandler;
import dev.mrflyn.veclink.ClientHandlers.VecLinkClient;
import org.simpleyaml.configuration.file.YamlFile;



public class Main implements GlobalInterface{

    public static MainConfig config;
    public static VecLinkClient client;
    public static GlobalInterface gi;
    public static PacketHandler pi;

    public static void main(String[] args){
        gi = new Main();
        pi = new PacketHandlerStandalone();
        enable();
    }

    public static void enable(){
        config = new MainConfig(new YamlFile(gi.getConfigLocation()+"VecLinkClient.yml"), new YamlFile(gi.getConfigLocation()+"messages.yml"));
        config.init();
        String ip = config.getMainConfig().getString("host").split(":")[0];
        int port = Integer.parseInt(config.getMainConfig().getString("host").split(":")[1]);
        client = new VecLinkClient(ip, port);
        client.start();
    }

    @Override
    public <T> void log(T message) {
        System.out.println("[VecLinkClient] "+message.toString());
    }

    @Override
    public <T> void debug(T message) {

        if (config.getMainConfig().getBoolean("debug"))
            System.out.println("[VecLinkClient][Debug] "+message.toString());

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
