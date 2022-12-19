package me.xginko.netherceiling.config;

import me.xginko.netherceiling.NetherCeiling;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class Config {
    private FileConfiguration config;
    private final File configPath;
    public final Locale default_lang;
    private final Logger logger;
    public final boolean auto_lang;
    public final double config_version;

    public Config() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        logger = NetherCeiling.getLog();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        // Config Version
        this.config_version = getDouble("config-version", 1.0);
        // Language Settings
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us").replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true);
    }

    public void saveConfig() {
        try {
            config.save(configPath);
            config = NetherCeiling.getInstance().getConfig();
        } catch (IOException e) {
            logger.severe("Failed to save configuration file! - " + e.getLocalizedMessage());
        }
    }

    public boolean getBoolean(String path, boolean def) {
        if (config.isSet(path)) return config.getBoolean(path, def);
        config.set(path, def);
        return def;
    }

    public String getString(String path, String def) {
        if (config.isSet(path)) return config.getString(path, def);
        config.set(path, def);
        return def;
    }

    public double getDouble(String path, Double def) {
        if (config.isSet(path)) return config.getDouble(path, def);
        config.set(path, def);
        return def;
    }

    public int getInt(String path, int def) {
        if (config.isSet(path)) return config.getInt(path, def);
        config.set(path, def);
        return def;
    }

    public List<String> getList(String path, List<String> def) {
        if (config.isSet(path)) return config.getStringList(path);
        config.set(path, def);
        return def;
    }

    public ConfigurationSection getConfigSection(String path, Map<String, Object> defaultKeyValue) {
        if (config.isConfigurationSection(path)) return config.getConfigurationSection(path);
        return config.createSection(path, defaultKeyValue);
    }
}
