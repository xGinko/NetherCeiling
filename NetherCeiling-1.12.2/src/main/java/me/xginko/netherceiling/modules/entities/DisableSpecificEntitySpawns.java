package me.xginko.netherceiling.modules.entities;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class DisableSpecificEntitySpawns implements NetherCeilingModule, Listener {

    private final HashSet<EntityType> disabledEntities = new HashSet<>();
    private final boolean useAsWhitelist;
    private final int ceilingY;

    public DisableSpecificEntitySpawns() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        Logger logger = NetherCeiling.getLog();
        List<String> configuredDisabledEntities = config.getList("entities.disable-specific-entity-spawns.entities", Arrays.asList("GHAST", "ZOMBIFIED_PIGLIN"));
        for (String configuredEntity : configuredDisabledEntities) {
            try {
                EntityType disabledEntity = EntityType.valueOf(configuredEntity);
                disabledEntities.add(disabledEntity);
            } catch (IllegalArgumentException e) {
                logger.warning("("+name()+") EntityType '"+configuredEntity+"' not recognized! Please use correct values from https://helpch.at/docs/1.12.2/org/bukkit/entity/EntityType.html");
            }
        }
        this.useAsWhitelist = config.getBoolean("entities.disable-specific-entity-spawns.use-as-whitelist-instead", false);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "disable-specific-entity-spawns";
    }

    @Override
    public String category() {
        return "entities";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("entities.disable-specific-entity-spawns.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyCreatureSpawning(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (entity.getLocation().getY() < ceilingY) return;

        if (useAsWhitelist) {
            if (!disabledEntities.contains(entity.getType())) {
                event.setCancelled(true);
            }
        } else {
            if (disabledEntities.contains(entity.getType())) {
                event.setCancelled(true);
            }
        }
    }
}
