package me.xginko.netherceiling.modules.portals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class PreventCreatingPortals implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;
    private final int ceilingY;

    public PreventCreatingPortals() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        this.shouldShowActionbar = config.getBoolean("portals.prevent-creating-portals.show-actionbar", true);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "prevent-creating-portals.";
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
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("portals.prevent-creating-portals.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyPortalCreation(PortalCreateEvent event) {
        if (!event.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (event.getBlocks().stream().noneMatch(blockState -> blockState.getY() > ceilingY+1)) return;

        event.setCancelled(true);

        if (shouldShowActionbar && event.getReason().equals(PortalCreateEvent.CreateReason.FIRE)) {
            for (Player player : event.getBlocks().get(1).getLocation().getNearbyPlayers(5,5,5)) {
                player.sendActionBar(NetherCeiling.getLang(player.getLocale()).portals_cant_create_on_ceiling);
            }
        }
    }
}
