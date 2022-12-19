package me.xginko.netherceiling.modules;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.building.*;
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

        for (NetherCeilingModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
