package me.xginko.netherceiling.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.xginko.netherceiling.NetherCeiling;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class Config {

    private ConfigFile config;
    private final File configFile;
    public final Locale default_lang;
    private final Logger logger;
    public final boolean auto_lang, safe_teleport_enabled;
    public final double config_version;
    public final int nether_ceiling_y, teleport_distance_in_blocks;

    public Config() {
        configFile = new File(NetherCeiling.getInstance().getDataFolder(), "config.yml");
        logger = NetherCeiling.getLog();
        createFiles();
        loadConfig();

        // Config Version and Header
        this.config_version = getDouble("config-version", 1.0);
        config.addComment("config-version", "     _   _      _   _                ____     _ _ _                    ");
        config.addComment("config-version", "    | \\ | | ___| |_| |__   ___ _ __ / ___|___(_) (_)_ __   __ _       ");
        config.addComment("config-version", "    |  \\| |/ _ \\ __| '_ \\ / _ \\ '__| |   / _ \\ | | | '_ \\ / _` | ");
        config.addComment("config-version", "    | |\\  |  __/ |_| | | |  __/ |  | |__|  __/ | | | | | | (_| |      ");
        config.addComment("config-version", "    |_| \\_|\\___|\\__|_| |_|\\___|_|   \\____\\___|_|_|_|_| |_|\\__, |");
        config.addComment("config-version", "                                                by xGinko |___/        ");

        // Language Settings
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us", "The default language that will be used if auto-language is false or no matching language file was found.").replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true, "If set to true, will display messages based on client language");

        // General Settings
        this.nether_ceiling_y = getInt("general.nether-ceiling-y", 127, "The Y-level at which the nether ceiling generates the last layer of bedrock on your server.");

        // Teleport Settings
        this.safe_teleport_enabled = getBoolean("teleport-from-nether-ceiling-settings.safely-teleport-players", true, "This option can also be enabled via permission: netherceiling.safeteleport.\nTeleports player downwards, creating a safe airpocket where they can then free themselves.\nChecks for air below feet and lava in any direction harmful to the player.");
        this.teleport_distance_in_blocks = getInt("teleport-from-nether-ceiling-settings.downwards-distance-in-blocks", 7, "The distance in blocks the player will be teleported downwards. Recommended to leave\nat 7 if using in combination with safe teleport, so players don't abuse the plugin\nto delete berdock.");
    }

    private void createFiles() {
        try {
            File parent = new File(configFile.getParent());
            if (!parent.exists()) {
                if (!parent.mkdir())
                    logger.severe("Unable to create plugin directory.");
            }
            if (!configFile.exists()) {
                if (!configFile.createNewFile())
                    logger.severe("Unable to create config file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            config = ConfigFile.loadConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            logger.severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
    }

    public String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public double getDouble(String path, Double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, Double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue) {
        config.makeSectionLenient(path);
        config.addDefault(path, defaultKeyValue);
        return config.getConfigSection(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue, String comment) {
        config.makeSectionLenient(path);
        config.addDefault(path, defaultKeyValue, comment);
        return config.getConfigSection(path);
    }

    public void addComment(String path, String comment) {
        config.addComment(path, comment);
    }
}
