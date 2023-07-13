package me.xginko.netherceiling.modules.illegals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.HashSet;

public class RemoveAllBlocksPeriodically implements NetherCeilingModule, Runnable {

    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS;
    private final double pauseTPS;
    private final long checkPeriod;
    private final int ceilingY;

    public RemoveAllBlocksPeriodically() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        this.checkPeriod = config.getInt("illegals.remove-all-blocks.periodically.check-period-in-seconds", 30) * 20L;
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-all-blocks.periodically.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-all-blocks.periodically.pause-TPS", 14.0);
        this.exemptedWorlds.addAll(config.getList("illegals.remove-all-blocks.periodically.exempted-worlds", Arrays.asList(
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
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, checkPeriod, checkPeriod);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("illegals.remove-all-blocks.periodically.enable", false);
    }

    @Override
    public void run() {
        if (checkShouldPauseOnLowTPS && NetherCeiling.getTPS() <= pauseTPS) return;

        for (World world : Bukkit.getWorlds()) {
            if (!exemptedWorlds.contains(world.getName())) {
                if (world.getEnvironment().equals(World.Environment.NETHER)) {
                    final int maxY = world.getMaxHeight();
                    for (Chunk chunk : world.getLoadedChunks()) {
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
            }
        }
    }
}
