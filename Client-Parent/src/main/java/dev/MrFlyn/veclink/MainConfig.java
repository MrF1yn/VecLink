package dev.mrflyn.veclink;

import org.simpleyaml.configuration.file.YamlFile;

public class MainConfig {


    private YamlFile mainConfig;
    private YamlFile languageConfig;

    public MainConfig(YamlFile mainFile, YamlFile languageFile){
        mainConfig = mainFile;
        languageConfig = languageFile;
    }

    public void init(){
        try {
            if (!mainConfig.exists()) {
                Main.gi.debug("New file has been created: " + mainConfig.getFilePath() + "\n");

                mainConfig.createNewFile(true);
                mainConfig.addDefault("host", "0.0.0.0:8000");
                mainConfig.addDefault("debug", true);
                mainConfig.addDefault("client-id", "TestClient");
                mainConfig.addDefault("secret-code", "secret-test-code");

                mainConfig.save();

            } else {
                Main.gi.debug(mainConfig.getFilePath() + " already exists, loading configurations...\n");
            }
            mainConfig.load(); // Loads the entire file
            // If your file has comments inside you have to load it with yamlFile.loadWithComments()
        } catch (final Exception e) {
            e.printStackTrace();
        }
        try {
            if (!languageConfig.exists()) {
                Main.gi.debug("New file has been created: " + languageConfig.getFilePath() + "\n");

                languageConfig.createNewFile(true);

                for(ConfigPath c : ConfigPath.values()){
                    languageConfig.addDefault(c.toString(), c.getValue());
                }

                languageConfig.save();

            } else {
                Main.gi.debug(languageConfig.getFilePath() + " already exists, loading configurations...\n");
            }
            languageConfig.load(); // Loads the entire file
            // If your file has comments inside you have to load it with yamlFile.loadWithComments()
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public String getClientID(){
        return mainConfig.getString("client-id");
    }

    public YamlFile getMainConfig() {
        return mainConfig;
    }

    public YamlFile getLanguageConfig() {
        return languageConfig;
    }

}
