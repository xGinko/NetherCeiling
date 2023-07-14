package me.xginko.netherceiling.modules.general;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PreventTeleportingUp implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;
    private final int ceilingY;

    public PreventTeleportingUp() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("general.prevent-teleporting-up.enable", "Prevents players from using something like chorus fruits\nor enderpearls to teleport onto the ceiling.");
        this.shouldShowActionbar = config.getBoolean("general.prevent-teleporting-up.show-actionbar", true);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "prevent-teleporting-up";
    }

    @Override
    public String category() {
        return "general";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("general.prevent-teleporting-up.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyTeleportingToCeiling(PlayerTeleportEvent event) {
        Location teleportDestination = event.getTo();
        if (teleportDestination.getWorld().getEnvironment().equals(World.Environment.NETHER) && teleportDestination.getY() >= ceilingY) {
            Player player = event.getPlayer();
            if (player.hasPermission("netherceiling.bypass")) return;

            event.setCancelled(true);
            if (shouldShowActionbar) player.sendActionBar(NetherCeiling.getLang(player.locale()).general_cant_tp_to_ceiling);
        }
    }
}
