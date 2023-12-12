package me.xginko.netherceiling.modules.illegals;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;

public class RemoveAllBlocksPeriodically implements NetherCeilingModule {

    private final NetherCeiling plugin;
    private ScheduledTask scheduledTask;
    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS;
    private final double pauseTPS;
    private final long checkPeriod;
    private final int ceilingY;

    public RemoveAllBlocksPeriodically() {
        shouldEnable();
        this.plugin = NetherCeiling.getInstance();
        Config config = NetherCeiling.getConfiguration();
        this.checkPeriod = config.getInt("illegals.remove-all-blocks.periodically.check-period-in-seconds", 60) * 20L;
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-all-blocks.periodically.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-all-blocks.periodically.pause-TPS", 14.0);
        this.exemptedWorlds.addAll(config.getList("illegals.remove-all-blocks.periodically.exempted-worlds", List.of(
                "exampleworld1", "exampleworld2"
        )));
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "remove-all-blocks.periodically";
    }

    @Override
    public String category() {
        return "illegals";
    }

    @Override
    public void enable() {
        this.scheduledTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> run(), checkPeriod, checkPeriod);
    }

    @Override
    public void disable() {
        if (scheduledTask != null) scheduledTask.cancel();
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("illegals.remove-all-blocks.periodically.enable", false);
    }

    private void run() {
        if (checkShouldPauseOnLowTPS && NetherCeiling.getTPS() <= pauseTPS) return;

        for (World world : plugin.getServer().getWorlds()) {
            if (!exemptedWorlds.contains(world.getName())) {
                if (world.getEnvironment().equals(World.Environment.NETHER)) {
                    final int maxY = world.getMaxHeight();
                    for (Chunk chunk : world.getLoadedChunks()) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int y = ceilingY+1; y < maxY; y++) {
                                    Block block = chunk.getBlock(x, y, z);
                                    plugin.getServer().getRegionScheduler().run(plugin, world, chunk.getX(), chunk.getZ(), removeBlock -> {
                                        if (!block.getType().equals(Material.AIR)) {
                                            block.setType(Material.AIR, false);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
