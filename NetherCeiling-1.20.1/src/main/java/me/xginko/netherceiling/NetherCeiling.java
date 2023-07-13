package me.xginko.netherceiling;

import me.xginko.netherceiling.commands.NetherCeilingCommand;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.config.LanguageCache;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NetherCeiling extends JavaPlugin {

    private static NetherCeiling instance;
    private static Logger logger;
    private static Config config;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static double tps;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        // Fancy enable
        logger.info(" ");
        logger.info("     _   _      _   _                ____     _ _ _                    ");
        logger.info("    | \\ | | ___| |_| |__   ___ _ __ / ___|___(_) (_)_ __   __ _       ");
        logger.info("    |  \\| |/ _ \\ __| '_ \\ / _ \\ '__| |   / _ \\ | | | '_ \\ / _` | ");
        logger.info("    | |\\  |  __/ |_| | | |  __/ |  | |__|  __/ | | | | | | (_| |      ");
        logger.info("    |_| \\_|\\___|\\__|_| |_|\\___|_|   \\____\\___|_|_|_|_| |_|\\__, |");
        logger.info("                                                by xGinko |___/        ");
        logger.info(" ");

        logger.info("Loading Translations");
        reloadLang();

        logger.info("Loading Config");
        reloadConfiguration();

        logger.info("Registering Commands");
        NetherCeilingCommand.reloadCommands();

        logger.info("Loading Metrics");
        new Metrics(this, 17203);

        // Scheduled TPS checker
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            new Thread(() -> tps = getServer().getTPS()[0]).start();
        }, 1, 1, TimeUnit.SECONDS);

        logger.info("Done.");
    }

    public static NetherCeiling getInstance()  {
        return instance;
    }
    public static Config getConfiguration() {
        return config;
    }
    public static Logger getLog() {
        return logger;
    }
    public static double getTPS() {
        return tps;
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (config.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(config.default_lang.toString().toLowerCase()));
        } else {
            return languageCacheMap.get(config.default_lang.toString().toLowerCase());
        }
    }

    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player player) {
            return getLang(player.locale());
        } else {
            return getLang(config.default_lang);
        }
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
        NetherCeilingCommand.reloadCommands();
    }

    public void reloadConfiguration() {
        config = new Config();
        NetherCeilingModule.reloadModules();
        config.saveConfig();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(instance.getDataFolder() + File.separator + "lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                logger.info(String.format("Found language file for %s", localeString));
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if(!languageCacheMap.containsKey(localeString)) {
                        logger.info(String.format("Found language file for %s", localeString));
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles() {
        try {
            Set<String> languageFiles = new HashSet<>();
            JarFile jar = new JarFile(this.getFile());
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String path = entry.getName();
                if (path.startsWith("lang/") && path.endsWith(".yml")) {
                    languageFiles.add(path);
                }
            }
            return languageFiles;
        } catch (IOException e) {
            return new HashSet<>();
        }
    }
}