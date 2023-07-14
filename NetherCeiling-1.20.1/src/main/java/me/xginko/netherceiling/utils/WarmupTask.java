package me.xginko.netherceiling.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.netherceiling.NetherCeiling;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class WarmupTask implements Runnable {

    private ScheduledTask task;

    public synchronized void stop() {
        this.task.cancel();
    }

    public synchronized ScheduledTask start(@NotNull Player player) {
        return this.setup(player.getScheduler().runAtFixedRate(NetherCeiling.getInstance(), warmupTask -> this.run(), null, 1L, 20L));
    }

    private @NotNull ScheduledTask setup(ScheduledTask task) {
        this.task = task;
        return task;
    }
}
