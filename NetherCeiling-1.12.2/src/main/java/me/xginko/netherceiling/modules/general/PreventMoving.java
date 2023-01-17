package me.xginko.netherceiling.modules.general;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.xginko.netherceiling.utils.CeilingUtils.*;

public class PreventMoving implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar, teleportPlayerDownwards;
    private final int ceilingY;

    public PreventMoving() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("general.prevent-moving.enable", "Players won't be able to move on the ceiling.");
        this.shouldShowActionbar = config.getBoolean("general.prevent-moving.show-actionbar", true);
        this.teleportPlayerDownwards = config.getBoolean("general.prevent-moving.teleport-down-on-move", false);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "prevent-moving";
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
        return NetherCeiling.getConfiguration().getBoolean("general.prevent-moving.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyMovingOnCeiling(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;

        event.setCancelled(true);
        if (player.isInsideVehicle()) player.leaveVehicle();
        if (player.isGliding()) player.setGliding(false);

        if (teleportPlayerDownwards) {
            teleportFromCeiling(player);
            if (shouldShowActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.getLocale()).general_cant_be_on_ceiling)
            );
        } else {
            if (shouldShowActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.getLocale()).general_cant_move_on_ceiling)
            );
        }
    }
}
