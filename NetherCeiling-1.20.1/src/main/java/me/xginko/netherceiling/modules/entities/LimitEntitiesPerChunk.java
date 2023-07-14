package me.xginko.netherceiling.modules.entities;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LimitEntitiesPerChunk implements NetherCeilingModule, Runnable, Listener {

    private final HashMap<EntityType, Integer> entityLimits = new HashMap<>();
    private final long checkPeriod;
    private final boolean logIsEnabled;
    private final int ceilingY;

    public LimitEntitiesPerChunk() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("entities.entity-limits-per-ceiling-chunk.enable", "Only counts entities above the nether ceiling.");
        this.logIsEnabled = config.getBoolean("entities.entity-limits-per-ceiling-chunk.log", true);
        this.checkPeriod = config.getInt("entities.entity-limits-per-ceiling-chunk.check-period-in-ticks", 20);
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("MULE", 5);
        defaults.put("PIG", 4);
        defaults.put("HORSE", 2);
        defaults.put("BOAT", 4);
        ConfigSection section = config.getConfigSection("entities.entity-limits-per-ceiling-chunk.entities", defaults);
        for (String configuredEntity : section.getKeys(false)) {
            try {
                EntityType limitedEntity = EntityType.valueOf(configuredEntity);
                Integer maxAmountPerChunk = Integer.valueOf(section.getString(configuredEntity));
                this.entityLimits.put(limitedEntity, maxAmountPerChunk);
            } catch (NumberFormatException e) {
                LogUtils.integerNotRecognized(Level.WARNING, name(), configuredEntity);
            } catch (IllegalArgumentException e) {
                LogUtils.entityTypeNotRecognized(Level.WARNING, name(), configuredEntity);
            }
        }
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "entity-limits-per-ceiling-chunk";
    }

    @Override
    public String category() {
        return "entities";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, checkPeriod, checkPeriod);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("entities.entity-limits-per-ceiling-chunk.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onSpawn(EntitySpawnEvent event) {
        EntityType spawnedType = event.getEntityType();
        if (!entityLimits.containsKey(spawnedType)) return;

        final int maxAllowed = entityLimits.get(spawnedType);
        for (Entity entity : event.getEntity().getChunk().getEntities()) {
            int count = 0;
            if (entity.getType().equals(spawnedType) && entity.getLocation().getY() > ceilingY) {
                count++;
                if (count > maxAllowed) {
                    entity.remove();
                    if (logIsEnabled) LogUtils.moduleLog(Level.INFO, name(), "Removed entity " + entity.getType()
                            + " at x:" + entity.getLocation().getX() + " y:" + entity.getLocation().getY() + " z:" + entity.getLocation().getZ()
                            + " in "+entity.getWorld().getName()+" because reached limit of " + maxAllowed
                    );
                }
            }
        }
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NETHER)) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    for (Map.Entry<EntityType, Integer> limit : entityLimits.entrySet()) {

                        final int maxAllowed = limit.getValue();
                        int count = 0;

                        for (Entity entity : chunk.getEntities()) {
                            if (entity.getType().equals(limit.getKey()) && entity.getLocation().getY() > ceilingY) {
                                count++;
                                if (count > maxAllowed) {
                                    entity.remove();
                                    if (logIsEnabled) LogUtils.moduleLog(Level.INFO, name(), "Removed entity " + entity.getType()
                                            + " at x:" + entity.getLocation().getX() + " y:" + entity.getLocation().getY() + " z:" + entity.getLocation().getZ()
                                            + " in "+entity.getWorld().getName()+" because reached limit of " + maxAllowed
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
