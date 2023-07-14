package me.xginko.netherceiling.modules.entities;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class DisableNonPlayerSpawns implements NetherCeilingModule, Listener {

    private final int ceilingY;

    public DisableNonPlayerSpawns() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("entities.disable-all-non-player-entity-spawns", "Prevent all entities from spawning on the nther ceiling.");
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "disable-all-non-player-entity-spawns";
    }

    @Override
    public String category() {
        return "entities";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("entities.disable-all-non-player-entity-spawns", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyCreatureSpawning(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) return;
        if (!entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (entity.getLocation().getY() > ceilingY) {
            event.setCancelled(true);
        }
    }
}
