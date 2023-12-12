package me.xginko.netherceiling.utils;

import me.xginko.netherceiling.NetherCeiling;

import java.util.logging.Level;

public class LogUtils {
    public static void moduleLog(Level logLevel, String moduleName, String logMessage) {
        NetherCeiling.getLog().log(logLevel, "<" + moduleName + "> " + logMessage);
    }

    public static void materialNotRecognized(Level logLevel, String moduleName, String material) {
        moduleLog(logLevel, moduleName, "Material '" + material + "' not recognized. Please use the correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/Material.html");
    }

    public static void entityTypeNotRecognized(Level logLevel, String moduleName, String entityType) {
        moduleLog(logLevel, moduleName, "EntityType '" + entityType + "' not recognized. Please use the correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/entity/EntityType.html");
    }

    public static void enchantmentNotRecognized(Level logLevel, String moduleName, String enchantment) {
        moduleLog(logLevel, moduleName, "Enchantment '" + enchantment + "' not recognized. Please use the correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/enchantments/Enchantment.html");
    }

    public static void integerNotRecognized(Level logLevel, String moduleName, String element) {
        moduleLog(logLevel, moduleName, "The configured amount for "+element+" is not an integer.");
    }

    public static void potionEffectNotRecognized(Level logLevel, String moduleName, String effectType) {
        moduleLog(logLevel, moduleName, "PotionEffectType '" + effectType + "' not recognized. Please use the correct enums from https://jd.papermc.io/paper/1.20/index.html?org/bukkit/potion/PotionEffectType.html");
    }
}