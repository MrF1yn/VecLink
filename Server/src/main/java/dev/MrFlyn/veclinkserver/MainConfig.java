package dev.mrflyn.veclinkserver;


import org.simpleyaml.configuration.file.YamlFile;

import java.util.Arrays;

public class MainConfig {


    private YamlFile mainConfig;

    public MainConfig(YamlFile file){
        mainConfig = file;
    }

    public void init(){
        try {
            if (!mainConfig.exists()) {
                System.out.println("New file has been created: " + mainConfig.getFilePath() + "\n");

                mainConfig.createNewFile(true);
                mainConfig.addDefault("port", 8000);
                mainConfig.addDefault("debug", true);
                mainConfig.addDefault("secret-code", "secret-test-code");
                mainConfig.addDefault("maximum-allowed-connections", 10);
                mainConfig.addDefault("maximum-allowed-connections", 10);
                mainConfig.addDefault("console-spam-detection.prevent-console-spam", false);
                mainConfig.addDefault("console-spam-detection.minimum-time-between-msg", 25L);
                mainConfig.addDefault("console-spam-detection.max-violations", 5);
                mainConfig.addDefault("console-spam-detection.reset-violations-after", 5000L);
                mainConfig.addDefault("ip-whitelist.enabled", false);
                mainConfig.addDefault("ip-whitelist.whitelisted-ips", Arrays.asList("1.1.1.1"));
                mainConfig.addDefault("groups.default", Arrays.asList("bedwars", "survival", "arcade"));
                mainConfig.addDefault("chat-groups.admin.format", "[[client-id]][%luckperms_prefix%][player]: [message]");
                mainConfig.addDefault("chat-groups.staff.format", "[[client-id]][%luckperms_prefix%][player]: [message]");
                mainConfig.save();

            } else {
                System.out.println(mainConfig.getFilePath() + " already exists, loading configurations...\n");
            }
            mainConfig.load(); // Loads the entire file
            // If your file has comments inside you have to load it with yamlFile.loadWithComments()
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public  YamlFile getMainConfig() {
        return mainConfig;
    }

}
