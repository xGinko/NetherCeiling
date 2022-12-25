package me.xginko.netherceiling.modules.portals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PreventUsingPortalsToCeiling implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;

    public PreventUsingPortalsToCeiling() {
        this.shouldShowActionbar = NetherCeiling.getConfiguration().getBoolean("portals.prevent-using-portals-to-ceiling.show-actionbar", true);
    }

    @Override
    public String name() {
        return "prevent-using-portals-to-ceiling";
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
        return NetherCeiling.getConfiguration().getBoolean("portals.prevent-using-portals-to-ceiling.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyPortalsToCeiling(PlayerTeleportEvent event) {
        if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) return;
        Player player = event.getPlayer();
        if (player.hasPermission("netherceilingplus.bypass")) return;

        Location to = event.getTo();
        if (!to.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (to.getY() < 127) return;

        event.setCancelled(true);
        if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                NetherCeiling.getLang(player.locale()).portals_cant_use_to_ceiling)
        ));
    }
}
