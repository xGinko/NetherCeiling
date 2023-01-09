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
import org.bukkit.event.world.PortalCreateEvent;

public class PreventCreatingPortals implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;

    public PreventCreatingPortals() {
        this.shouldShowActionbar = NetherCeiling.getConfiguration().getBoolean("portals.prevent-creating-portals.show-actionbar", true);
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
        if (event.getBlocks().stream().noneMatch(blockState -> blockState.getY() > 128)) return;

        if (event.getEntity() instanceof Player player) {
            if (player.hasPermission("netherceiling.bypass")) return;
            event.setCancelled(true);
            if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.locale()).portals_cant_create_on_ceiling)
            ));
        } else {
            event.setCancelled(true);
        }
    }
}
