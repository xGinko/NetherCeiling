package me.xginko.netherceiling.modules.entities;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class DisableSpecificEntitySpawns implements NetherCeilingModule, Listener {

    private final HashSet<EntityType> disabledEntities = new HashSet<>();
    private final boolean useAsWhitelist;
    private final int ceilingY;

    public DisableSpecificEntitySpawns() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        List<String> configuredDisabledEntities = config.getList("entities.disable-specific-entity-spawns.entities", List.of("GHAST", "ZOMBIFIED_PIGLIN"));
        for (String configuredEntity : configuredDisabledEntities) {
            try {
                EntityType disabledEntity = EntityType.valueOf(configuredEntity);
                disabledEntities.add(disabledEntity);
            } catch (IllegalArgumentException e) {
                LogUtils.entityTypeNotRecognized(Level.WARNING, name(), configuredEntity);
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
        if (useAsWhitelist) {
            if (!disabledEntities.contains(event.getEntityType())) {
                Entity entity = event.getEntity();
                if (!entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
                if (entity.getLocation().getY() < ceilingY) return;
                event.setCancelled(true);
            }
        } else {
            if (disabledEntities.contains(event.getEntityType())) {
                Entity entity = event.getEntity();
                if (!entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
                if (entity.getLocation().getY() < ceilingY) return;
                event.setCancelled(true);
            }
        }
    }
}
