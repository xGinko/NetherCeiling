package me.xginko.netherceiling.modules.potions;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;

public class RemoveSpecificPotionEffects implements NetherCeilingModule, Listener {

    private final NetherCeiling plugin;
    private final HashSet<PotionEffectType> blacklistedPotionEffectTypes = new HashSet<>();
    private final boolean shouldShowActionbar, useAsWhitelistInstead;
    private final int ceilingY;

    public RemoveSpecificPotionEffects() {
        shouldEnable();
        this.plugin = NetherCeiling.getInstance();
        Config config = NetherCeiling.getConfiguration();
        this.shouldShowActionbar = config.getBoolean("potions.remove-specific-potion-effects.show-actionbar", false);
        this.useAsWhitelistInstead = config.getBoolean("potions.remove-specific-potion-effects.use-as-whitelist-instead", false);
        for (String potionEffectEntry : config.getList("potions.remove-specific-potion-effects.potion-effects", Collections.singletonList("SPEED"))) {
            PotionEffectType potionEffectFromName = PotionEffectType.getByName(potionEffectEntry);
            if (potionEffectFromName != null) {
                this.blacklistedPotionEffectTypes.add(potionEffectFromName);
            } else {
                LogUtils.potionEffectNotRecognized(Level.WARNING, name(), potionEffectEntry);
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
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("potions.remove-specific-potion-effects.enable", false) && !blacklistedPotionEffectTypes.isEmpty();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;
        if (player.hasPermission("netherceiling.bypass")) return;

        plugin.getServer().getRegionScheduler().run(plugin, player.getLocation(), nerfPotions -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                PotionEffectType potionEffectType = effect.getType();
                if (useAsWhitelistInstead) {
                    if (!blacklistedPotionEffectTypes.contains(potionEffectType)) {
                        player.removePotionEffect(potionEffectType);
                        if (shouldShowActionbar)
                            player.sendActionBar(NetherCeiling.getLang(player.locale()).potions_effect_removed);
                    }
                } else {
                    if (blacklistedPotionEffectTypes.contains(potionEffectType)) {
                        player.removePotionEffect(potionEffectType);
                        if (shouldShowActionbar)
                            player.sendActionBar(NetherCeiling.getLang(player.locale()).potions_effect_removed);
                    }
                }
            }
        });
    }
}
