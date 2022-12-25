package me.xginko.netherceiling.modules.portals;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PreventUsingPortalsOnCeiling implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;

    public PreventUsingPortalsOnCeiling() {
        this.shouldShowActionbar = NetherCeiling.getConfiguration().getBoolean("portals.prevent-using-portals-on-ceiling.show-actionbar", true);
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
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("portals.prevent-using-portals-on-ceiling.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyPortalUsage(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < 127) return;

        event.setCancelled(true);
        if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                NetherCeiling.getLang(player.locale()).portals_cant_use_on_ceiling)
        ));
    }
}
