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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class NerfSpecificPotionEffects implements NetherCeilingModule, Listener {
    
    private final HashSet<PotionEffect> potionEffectLimits = new HashSet<>();
    private final boolean shouldShowActionbar;

    public NerfSpecificPotionEffects() {
        Config config = NetherCeiling.getConfiguration();
        this.shouldShowActionbar = config.getBoolean("potions.nerf-specific-potion-effects.show-actionbar", false);
        List<String> configuredPotionEffects = config.getList("potions.nerf-specific-potion-effects.potion-effects", Arrays.asList("SPEED,1,1200", "REGENERATION,1,200"));
        Logger logger = NetherCeiling.getInstance().getLogger();
        for (String configListEntry : configuredPotionEffects) {
            String[] configEntry = configListEntry.split(",");
            PotionEffectType potionEffectFromName = PotionEffectType.getByName(configEntry[0]);
            if (potionEffectFromName != null) {
                potionEffectLimits.add(new PotionEffect(
                        potionEffectFromName, (Integer.parseInt(configEntry[2])), Integer.parseInt(configEntry[1])
                ));
            } else {
                logger.warning("("+name()+") PotionEffectType '"+configEntry[0]+"' not recognized. Please use correct values from https://helpch.at/docs/1.12.2/index.html?org/bukkit/potion/PotionEffectType.html");
            }
        }
    }

    @Override
    public String name() {
        return "nerf-specific-potion-effects";
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
        return NetherCeiling.getConfiguration().getBoolean("potions.nerf-specific-potion-effects.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < 127) return;

        HashSet<PotionEffect> activeEffects = new HashSet<>(player.getActivePotionEffects());
        if (activeEffects.isEmpty()) return;

        for (PotionEffect activePotionEffect : activeEffects) {
            PotionEffectType activePotionEffectType = activePotionEffect.getType();

            for (PotionEffect potionEffectLimit : potionEffectLimits) {
                if (potionEffectLimit.getType().equals(activePotionEffectType)) {
                    if (activePotionEffect.getAmplifier() > potionEffectLimit.getAmplifier()) {
                        player.removePotionEffect(activePotionEffectType);
                        player.addPotionEffect(new PotionEffect(activePotionEffectType, potionEffectLimit.getAmplifier(), activePotionEffect.getDuration()));
                    }
                    if (activePotionEffect.getDuration() > potionEffectLimit.getDuration()) {
                        player.removePotionEffect(activePotionEffectType);
                        player.addPotionEffect(new PotionEffect(activePotionEffectType, activePotionEffect.getAmplifier(), potionEffectLimit.getDuration()));
                    }
                    if (shouldShowActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                            NetherCeiling.getLang(player.getLocale()).potions_effect_nerfed)
                    );
                }
            }
        }
    }
}
