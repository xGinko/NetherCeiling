package me.xginko.netherceiling;

import me.xginko.netherceiling.commands.NetherCeilingCmd;
import me.xginko.netherceiling.commands.UnstuckCmd;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.config.LanguageCache;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        logger.info(" ");
        logger.info(" NetherCeiling ");
        logger.info("   by xGinko   ");
        logger.info(" ");
        logger.info(" ");

        // Load lang and config
        logger.info("Loading Translations");
        reloadLang();
        logger.info("Loading Config");
        reloadNetherCeilingConfig();

        // Register commands
        logger.info("Registering Commands");
        getCommand("netherceiling").setExecutor(new NetherCeilingCmd());
        getCommand("unstuck").setExecutor(new UnstuckCmd());

        // Metrics
        logger.info("Loading Metrics");
        new Metrics(this, 17203);

        // Resource-friendly TPS checker
        ScheduledExecutorService schedulerTPS = Executors.newScheduledThreadPool(1);
        schedulerTPS.scheduleAtFixedRate(() -> {
            Thread thread = new Thread(() -> tps = getServer().getTPS()[0]);
            thread.start();
        }, 2, 1, TimeUnit.SECONDS);

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

    public void reloadNetherCeilingConfig() {
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

    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", Scanners.Resources);
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }
}
