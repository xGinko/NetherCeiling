package me.xginko.netherceiling.modules.illegals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RemoveAllBlocksPeriodically implements NetherCeilingModule, Listener {

    private final HashSet<String> exemptedWorlds = new HashSet<>();
    private final boolean checkShouldPauseOnLowTPS;
    private final double pauseTPS;
    private final long checkPeriod;

    public RemoveAllBlocksPeriodically() {
        Config config = NetherCeiling.getConfiguration();
        this.checkPeriod = config.getInt("illegals.remove-all-blocks.periodically.check-period-in-seconds", 30) * 20L;
        this.checkShouldPauseOnLowTPS = config.getBoolean("illegals.remove-all-blocks.periodically.pause-on-low-TPS", true);
        this.pauseTPS = config.getDouble("illegals.remove-all-blocks.periodically.pause-TPS", 14.0);
        this.exemptedWorlds.addAll(config.getList("illegals.remove-all-blocks.periodically.exempted-worlds", Arrays.asList(
                "exampleworld1", "exampleworld2"
        )));
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
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, checkAndRemoveIllegalBlocks, 20L, checkPeriod);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("illegals.remove-all-blocks.periodically.enable", false);
    }

    private final Runnable checkAndRemoveIllegalBlocks = new Runnable() {
        @Override
        public void run() {
            if (checkShouldPauseOnLowTPS && (NetherCeiling.getTPS() <= pauseTPS)) return;

            for (World world : Bukkit.getWorlds()) {
                if (!world.getEnvironment().equals(World.Environment.NETHER)) return;
                if (exemptedWorlds.contains(world.getName())) return;

                int maxY = world.getMaxHeight();

                for (Chunk chunk : world.getLoadedChunks()) {
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
        }
    };
}
