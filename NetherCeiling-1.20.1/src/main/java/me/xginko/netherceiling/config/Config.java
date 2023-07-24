package me.xginko.netherceiling.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.xginko.netherceiling.NetherCeiling;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Config {

    private final ConfigFile config;
    public final Locale default_lang;
    public final boolean auto_lang, safe_teleport_enabled, warmup_is_enabled, warmup_cancel_on_move_or_dmg;
    public final int nether_ceiling_y, teleport_distance_in_blocks, warmup_delay_in_ticks;

    public Config() throws Exception {
        this.config = loadConfig(new File(NetherCeiling.getInstance().getDataFolder(), "config.yml"));
        structureConfig();

        // Language Settings
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us", "The default language that will be used if auto-language is false or no matching language file was found.").replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true, "If set to true, will display messages based on client language");

        // General Settings
        this.nether_ceiling_y = getInt("general.nether-ceiling-y", 127, "The Y-level at which the nether ceiling generates the last layer of bedrock on your server.");

        // Teleport Settings
        this.safe_teleport_enabled = getBoolean("teleport-from-nether-ceiling-settings.safely-teleport-players", true, "This option can also be enabled via permission: netherceiling.safeteleport.\nTeleports player downwards, creating a safe airpocket where they can then free themselves.\nChecks for air below feet and lava in any direction harmful to the player.");
        this.teleport_distance_in_blocks = getInt("teleport-from-nether-ceiling-settings.downwards-distance-in-blocks", 7, "The distance in blocks the player will be teleported downwards. Recommended to leave\nat 7 if using in combination with safe teleport, so players don't abuse the plugin\nto delete berdock.");
        this.warmup_is_enabled = getBoolean("teleport-from-nether-ceiling-settings.unstuck-cmd.warmup.enable", true, "Enable a warmup for the /unstuck command");
        this.warmup_delay_in_ticks = getInt("teleport-from-nether-ceiling-settings.unstuck-cmd.warmup.delay-in-seconds", 8, "How long in seconds the player should have to wait") * 20;
        this.warmup_cancel_on_move_or_dmg = getBoolean("teleport-from-nether-ceiling-settings.unstuck-cmd.warmup.cancel-on-move-or-damage", true, "Cancel warmup when player moves or gets damage");
    }

    private void structureConfig() {
        config.addDefault("config-version", 1.00);
        config.addComment(
                "config-version",
                """
                             _   _      _   _                ____     _ _ _                   \s
                            | \\ | | ___| |_| |__   ___ _ __ / ___|___(_) (_)_ __   __ _      \s
                            |  \\| |/ _ \\ __| '_ \\ / _ \\ '__| |   / _ \\ | | | '_ \\ / _` |\s
                            | |\\  |  __/ |_| | | |  __/ |  | |__|  __/ | | | | | | (_| |     \s
                            |_| \\_|\\___|\\__|_| |_|\\___|_|   \\____\\___|_|_|_|_| |_|\\__, |
                                                                        by xGinko |___/        \
                        """
        );
        createTitledSection("Language", "language");
        createTitledSection("General", "general");
        createTitledSection("Teleport", "teleport-from-nether-ceiling-settings");
        createTitledSection("Portals", "portals");
        createTitledSection("Building", "building");
        createTitledSection("Vehicles", "vehicles");
        createTitledSection("Fast Blocks", "fast-blocks");
        createTitledSection("Entities", "entities");
        createTitledSection("Illegals", "illegals");
        createTitledSection("Potions", "potions");
    }

    private ConfigFile loadConfig(File ymlFile) throws Exception {
        File parent = new File(ymlFile.getParent());
        if (!parent.exists())
            if (!parent.mkdir())
                NetherCeiling.getLog().severe("Unable to create plugin config directory.");
        if (!ymlFile.exists())
            ymlFile.createNewFile(); // Result can be ignored because this method only returns false if the file already exists
        return ConfigFile.loadConfig(ymlFile);
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (Exception e) {
            NetherCeiling.getLog().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public void createTitledSection(String title, String path) {
        config.addSection(title);
        config.addDefault(path, null);
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
