package me.xginko.netherceiling.modules.illegals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class RemoveSpecificBlocksOnChunkload implements NetherCeilingModule, Listener {

    private final HashSet<Material> blocksToRemove = new HashSet<>();
    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS, useAsWhitelistInstead;
    private final double pauseTPS;

    public RemoveSpecificBlocksOnChunkload() {
        Config config = NetherCeiling.getConfiguration();
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-specific-blocks.on-chunkload.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-specific-blocks.on-chunkload.pause-TPS", 16.0);
        this.useAsWhitelistInstead = config.getBoolean("illegals.remove-specific-blocks.on-chunkload.use-as-whitelist-instead", false);
        Logger logger = NetherCeiling.getLog();
        List<String> configuredBlocksToRemove = config.getList("illegals.remove-specific-blocks.on-chunkload.specific-blocks", List.of(
                "SOUL_SAND", "SOUL_SOIL", "ICE", "PACKED_ICE", "BLUE_ICE"
        ));
        for (String configuredBlock : configuredBlocksToRemove) {
            Material blockToRemove = Material.getMaterial(configuredBlock);
            if (blockToRemove != null) {
                blocksToRemove.add(blockToRemove);
            } else {
                logger.warning("("+name()+") Configured block '" + configuredBlock + "' is not a valid Material. Please use values from https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
            }
        }
        this.exemptedWorlds.addAll(config.getList("illegals.remove-specific-blocks.on-chunkload.exempted-worlds", List.of(
                "exampleworld1", "exampleworld2"
        )));
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
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        Config config = NetherCeiling.getConfiguration();
        if (config.getBoolean("illegals.remove-all-blocks.on-chunkload.enable", false)) return false;
        return config.getBoolean("illegals.remove-specific-blocks.on-chunkload.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk() || checkShouldPauseOnLowTPS && (NetherCeiling.getTPS() <= pauseTPS)) return;

        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        if (!world.getEnvironment().equals(World.Environment.NETHER)) return;
        if (exemptedWorlds.contains(world.getName())) return;

        int maxY = world.getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 128; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (useAsWhitelistInstead) {
                        if (!blocksToRemove.contains(block.getType())) {
                            block.setType(Material.AIR, false);
                        }
                    } else {
                        if (blocksToRemove.contains(block.getType())) {
                            block.setType(Material.AIR, false);
                        }
                    }
                }
            }
        }
    }
}
