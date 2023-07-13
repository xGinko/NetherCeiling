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

public class RemoveAllBlocksOnChunkload implements NetherCeilingModule, Listener {

    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS;
    private final double pauseTPS;
    private final int ceilingY;

    public RemoveAllBlocksOnChunkload() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("illegals.remove-all-blocks", "Use this if you want to remove everything players have placed above the ceiling.");
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-all-blocks.on-chunkload.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-all-blocks.on-chunkload.pause-TPS", 16.0);
        this.exemptedWorlds.addAll(config.getList("illegals.remove-all-blocks.on-chunkload.exempted-worlds", List.of(
                "exampleworld1", "exampleworld2"
        )));
        this.ceilingY = config.nether_ceiling_y;
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
        if (exemptedWorlds.contains(world.getName())) return;
        if (!world.getEnvironment().equals(World.Environment.NETHER)) return;

        final int maxY = world.getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = ceilingY+1; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (!block.getType().equals(Material.AIR)) {
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
    }
}
