package me.xginko.netherceiling.modules;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.building.*;
import me.xginko.netherceiling.modules.entities.DisableNonPlayerSpawns;
import me.xginko.netherceiling.modules.entities.DisableSpecificEntitySpawns;
import me.xginko.netherceiling.modules.entities.LimitEntitiesPerChunk;
import org.bukkit.event.HandlerList;

import java.util.HashSet;

public interface NetherCeilingModule {

    String name();
    String category();
    void enable();
    boolean shouldEnable();

    HashSet<NetherCeilingModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.clear();
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getScheduler().cancelTasks(plugin);
        HandlerList.unregisterAll(plugin);

        // Building
        modules.add(new BlacklistSpecificBlocks());
        modules.add(new CustomBuildHeight());
        modules.add(new EnablePlacingWater());
        modules.add(new EnableUsingBeds());
        modules.add(new LimitSpecificBlocks());

        // Entities
        modules.add(new DisableNonPlayerSpawns());
        modules.add(new DisableSpecificEntitySpawns());
        modules.add(new LimitEntitiesPerChunk());

        for (NetherCeilingModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
