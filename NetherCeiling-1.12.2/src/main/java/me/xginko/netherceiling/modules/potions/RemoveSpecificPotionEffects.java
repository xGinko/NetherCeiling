package me.xginko.netherceiling.modules.potions;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

public class RemoveSpecificPotionEffects implements NetherCeilingModule, Listener {

    private final HashSet<PotionEffectType> blacklistedPotionEffectTypes = new HashSet<>();
    private final boolean shouldShowActionbar, useAsWhitelistInstead;
    private final int ceilingY;

    public RemoveSpecificPotionEffects() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        this.shouldShowActionbar = config.getBoolean("potions.remove-specific-potion-effects.show-actionbar", false);
        this.useAsWhitelistInstead = config.getBoolean("potions.remove-specific-potion-effects.use-as-whitelist-instead", false);
        Logger logger = NetherCeiling.getLog();
        for (String potionEffectEntry : config.getList("potions.remove-specific-potion-effects.potion-effects", Collections.singletonList("SPEED"))) {
            PotionEffectType potionEffectFromName = PotionEffectType.getByName(potionEffectEntry);
            if (potionEffectFromName != null) {
                blacklistedPotionEffectTypes.add(potionEffectFromName);
            } else {
                logger.warning("("+name()+") PotionEffectType '"+potionEffectEntry+"' not recognized. Please use correct values from https://helpch.at/docs/1.12.2/index.html?org/bukkit/potion/PotionEffectType.html");
            }
        }
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "remove-specific-potion-effects";
    }

    @Override
    public String category() {
        return "potions";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        if (blacklistedPotionEffectTypes.isEmpty()) return false;
        return NetherCeiling.getConfiguration().getBoolean("potions.remove-specific-potion-effects.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;

        HashSet<PotionEffect> activeEffects = new HashSet<>(player.getActivePotionEffects());
        if (activeEffects.isEmpty()) return;

        for (PotionEffect effect : activeEffects) {
            PotionEffectType potionEffectType = effect.getType();
            if (useAsWhitelistInstead) {
                if (!blacklistedPotionEffectTypes.contains(potionEffectType)) {
                    player.removePotionEffect(potionEffectType);
                    if (shouldShowActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                            NetherCeiling.getLang(player.getLocale()).potions_effect_removed)
                    );
                }
            } else {
                if (blacklistedPotionEffectTypes.contains(potionEffectType)) {
                    player.removePotionEffect(potionEffectType);
                    if (shouldShowActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                            NetherCeiling.getLang(player.getLocale()).potions_effect_removed)
                    );
                }
            }
        }
    }
}
