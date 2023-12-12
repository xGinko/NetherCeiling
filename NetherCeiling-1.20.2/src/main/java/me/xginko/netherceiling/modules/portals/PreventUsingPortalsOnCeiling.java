package me.xginko.netherceiling.modules.portals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PreventUsingPortalsOnCeiling implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;
    private final int ceilingY;

    public PreventUsingPortalsOnCeiling() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("portals.prevent-using-portals-on-ceiling.enable", "Will cancel the teleport when a player attempts to use a portal on the ceiling.");
        this.shouldShowActionbar = config.getBoolean("portals.prevent-using-portals-on-ceiling.show-actionbar", true);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "prevent-using-portals-on-ceiling";
    }

    @Override
    public String category() {
        return "portals";
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
        return NetherCeiling.getConfiguration().getBoolean("portals.prevent-using-portals-on-ceiling.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyPortalUsage(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;
        if (player.hasPermission("netherceiling.bypass")) return;

        event.setCancelled(true);
        if (shouldShowActionbar)
            player.sendActionBar(NetherCeiling.getLang(player.locale()).portals_cant_use_on_ceiling);
    }
}
