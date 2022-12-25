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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RemoveAllBlocksOnChunkload implements NetherCeilingModule, Listener {

    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS;
    private final double pauseTPS;

    public RemoveAllBlocksOnChunkload() {
        Config config = NetherCeiling.getConfiguration();
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-all-blocks.on-chunkload.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-all-blocks.on-chunkload.pause-TPS", 16.0);
        this.exemptedWorlds.addAll(config.getList("illegals.remove-all-blocks.on-chunkload.exempted-worlds", Arrays.asList(
                "exampleworld1", "exampleworld2"
        )));
    }

    @Override
    public String name() {
        return "remove-all-blocks.on-chunkload";
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
        return NetherCeiling.getConfiguration().getBoolean("illegals.remove-all-blocks.on-chunkload.enable", false);
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
                    if (!block.getType().equals(Material.AIR)) {
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }
}
