package me.xginko.netherceiling.config;

import me.xginko.netherceiling.NetherCeiling;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageCache {
    private final FileConfiguration fileConfiguration;
    boolean addedMissing = false;
    public String noPermission;

    public LanguageCache(String lang) {
        NetherCeiling plugin = NetherCeiling.getInstance();
        File langFile = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        }
        try {
            fileConfiguration.load(langFile);

            // No Permission
            this.noPermission = getStringTranslation("no-permission", "You don't have permission to use this command.");


            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            NetherCeiling.getInstance().getLogger().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public String getStringTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }
}
