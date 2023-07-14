package me.xginko.netherceiling.modules.illegals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class RemoveSpecificBlocksOnChunkload implements NetherCeilingModule, Listener {

    private final NetherCeiling plugin;
    private final HashSet<Material> blocksToRemove = new HashSet<>();
    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS, useAsWhitelistInstead;
    private final double pauseTPS;
    private final int ceilingY;

    public RemoveSpecificBlocksOnChunkload() {
        shouldEnable();
        this.plugin = NetherCeiling.getInstance();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("illegals.remove-specific-blocks.on-chunkload", "Remove specific blocks that have been placed.");
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-specific-blocks.on-chunkload.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-specific-blocks.on-chunkload.pause-TPS", 16.0);
        this.useAsWhitelistInstead = config.getBoolean("illegals.remove-specific-blocks.on-chunkload.use-as-whitelist-instead", false);
        List<String> configuredBlocksToRemove = config.getList("illegals.remove-specific-blocks.on-chunkload.specific-blocks", List.of(
                "SOUL_SAND", "SOUL_SOIL", "ICE", "PACKED_ICE", "BLUE_ICE"
        ));
        for (String configuredMaterial : configuredBlocksToRemove) {
            try {
                Material materialToRemove = Material.valueOf(configuredMaterial);
                this.blocksToRemove.add(materialToRemove);
            } catch (IllegalArgumentException e) {
                LogUtils.materialNotRecognized(Level.WARNING, name(), configuredMaterial);
            }
        }
        this.exemptedWorlds.addAll(config.getList("illegals.remove-specific-blocks.on-chunkload.exempted-worlds", List.of(
                "exampleworld1", "exampleworld2"
        )));
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "remove-specific-blocks.on-chunkload";
    }

    @Override
    public String category() {
        return "illegals";
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
        Config config = NetherCeiling.getConfiguration();
        return  !config.getBoolean("illegals.remove-all-blocks.on-chunkload.enable", false)
                && config.getBoolean("illegals.remove-specific-blocks.on-chunkload.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk() || checkShouldPauseOnLowTPS && (NetherCeiling.getTPS() <= pauseTPS)) return;

        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        if (exemptedWorlds.contains(world.getName())) return;
        if (!world.getEnvironment().equals(World.Environment.NETHER)) return;

        final int maxY = world.getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = ceilingY+1; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (useAsWhitelistInstead) {
                        if (!blocksToRemove.contains(block.getType())) {
                            plugin.getServer().getRegionScheduler().run(
                                    plugin, world, chunk.getX(), chunk.getZ(), removeBlock -> block.setType(Material.AIR, false)
                            );
                        }
                    } else {
                        if (blocksToRemove.contains(block.getType())) {
                            plugin.getServer().getRegionScheduler().run(
                                    plugin, world, chunk.getX(), chunk.getZ(), removeBlock -> block.setType(Material.AIR, false)
                            );
                        }
                    }
                }
            }
        }
    }
}
