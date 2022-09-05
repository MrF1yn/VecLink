package dev.mrflyn.veclinkserver;


import org.simpleyaml.configuration.file.YamlFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class MainConfig {


    private YamlFile mainConfig;
    private YamlFile dbConfig;

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
            saveResource("database.yml", false);
            dbConfig = new YamlFile("database.yml");
            dbConfig.load();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public  YamlFile getMainConfig() {
        return mainConfig;
    }

    public YamlFile getDbConfig() {
        return dbConfig;
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        } else {
            try {
                URL url = this.getClass().getClassLoader().getResource(filename);
                if (url == null) {
                    return null;
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } catch (IOException var4) {
                return null;
            }
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in ");
            } else {
                File outFile = new File(resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        System.out.println("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    System.out.println("Could not save " + outFile.getName() + " to " + outFile+"\n"+var10.getMessage());
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }
}
