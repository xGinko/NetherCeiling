package me.xginko.netherceiling.modules.entities;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LimitEntitiesPerChunk implements NetherCeilingModule, Listener {

    private final Logger logger;
    private final HashMap<EntityType, Integer> entityLimits = new HashMap<>();
    private final long checkPeriod;
    private final boolean logIsEnabled;
    private final int ceilingY;

    public LimitEntitiesPerChunk() {
        shouldEnable();
        this.logger = NetherCeiling.getLog();
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
        if (section != null) {
            for (String configuredEntity : section.getKeys(false)) {
                Integer maxAmountPerChunk = Integer.valueOf(section.getString(configuredEntity));
                try {
                    EntityType limitedEntity = EntityType.valueOf(configuredEntity);
                    entityLimits.put(limitedEntity, maxAmountPerChunk);
                } catch (IllegalArgumentException e) {
                    logger.warning("(" + name() + ") EntityType '" + configuredEntity + "' not recognized! Please use correct values from https://helpch.at/docs/1.12.2/org/bukkit/entity/EntityType.html");
                }
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
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, checkAndRemoveCustom, 20L, checkPeriod);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("entities.entity-limits-per-ceiling-chunk.enable", false);
    }

    private final Runnable checkAndRemoveCustom = new Runnable() {
        @Override
        public void run() {
            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment().equals(World.Environment.NETHER)) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        for (Map.Entry<EntityType, Integer> limit : entityLimits.entrySet()) {
                            Integer maxAllowed = limit.getValue();
                            int count = 0;
                            for (Entity entity : chunk.getEntities()) {
                                if (entity.getType().equals(limit.getKey())) {
                                    if (entity.getLocation().getY() > ceilingY) {
                                        if (count > maxAllowed) {
                                            entity.remove();
                                            if (logIsEnabled) logger.info("("+name()+") Removed entity " + entity.getType()
                                                    + " at " + entity.getLocation() + " because reached limit of " + maxAllowed
                                            );
                                        }
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };
}
