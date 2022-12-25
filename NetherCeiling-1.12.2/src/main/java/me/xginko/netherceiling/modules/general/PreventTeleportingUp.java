package me.xginko.netherceiling.modules.general;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PreventTeleportingUp implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar;

    public PreventTeleportingUp() {
        this.shouldShowActionbar = NetherCeiling.getConfiguration().getBoolean("general.prevent-teleporting-up.show-actionbar", true);
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
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("general.prevent-teleporting-up.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyTeleportingToCeiling(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;

        if (event.getFrom().getY() <= 127 && event.getTo().getY() >= 127) {
            event.setCancelled(true);
            if (shouldShowActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.getLocale()).general_cant_tp_to_ceiling)
            );
        }
    }
}
