package me.xginko.netherceiling.modules;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.building.*;
import me.xginko.netherceiling.modules.entities.DisableNonPlayerSpawns;
import me.xginko.netherceiling.modules.entities.DisableSpecificEntitySpawns;
import me.xginko.netherceiling.modules.entities.LimitEntitiesPerChunk;
import me.xginko.netherceiling.modules.fastblocks.PlayerOnFastBlocks;
import me.xginko.netherceiling.modules.fastblocks.VehicleOnFastBlocks;
import me.xginko.netherceiling.modules.general.PreventBuilding;
import me.xginko.netherceiling.modules.general.PreventMoving;
import me.xginko.netherceiling.modules.general.PreventTeleportingUp;
import me.xginko.netherceiling.modules.illegals.RemoveAllBlocksOnChunkload;
import me.xginko.netherceiling.modules.illegals.RemoveAllBlocksPeriodically;
import me.xginko.netherceiling.modules.illegals.RemoveSpecificBlocksOnChunkload;
import me.xginko.netherceiling.modules.illegals.RemoveSpecificBlocksPeriodically;
import me.xginko.netherceiling.modules.portals.PreventCreatingPortals;
import me.xginko.netherceiling.modules.portals.PreventUsingPortalsOnCeiling;
import me.xginko.netherceiling.modules.portals.PreventUsingPortalsToCeiling;
import me.xginko.netherceiling.modules.potions.NerfSpecificPotionEffects;
import me.xginko.netherceiling.modules.potions.RemoveSpecificPotionEffects;
import me.xginko.netherceiling.modules.vehicles.PreventUsingSpecificVehicles;
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

        // Fast Blocks
        modules.add(new PlayerOnFastBlocks());
        modules.add(new VehicleOnFastBlocks());

        // General
        modules.add(new PreventBuilding());
        modules.add(new PreventMoving());
        modules.add(new PreventTeleportingUp());

        // Illegals
        modules.add(new RemoveAllBlocksOnChunkload());
        modules.add(new RemoveAllBlocksPeriodically());
        modules.add(new RemoveSpecificBlocksOnChunkload());
        modules.add(new RemoveSpecificBlocksPeriodically());

        // Portals
        modules.add(new PreventCreatingPortals());
        modules.add(new PreventUsingPortalsOnCeiling());
        modules.add(new PreventUsingPortalsToCeiling());

        // Potions
        modules.add(new NerfSpecificPotionEffects());
        modules.add(new RemoveSpecificPotionEffects());

        // Vehicles
        modules.add(new PreventUsingSpecificVehicles());

        for (NetherCeilingModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
