package me.xginko.netherceiling.modules.general;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static me.xginko.netherceiling.utils.CeilingUtils.*;

public class PreventBuilding implements NetherCeilingModule, Listener {

    private final boolean shouldShowActionbar, teleportPlayerDownwards;

    public PreventBuilding() {
        Config config = NetherCeiling.getConfiguration();
        this.shouldShowActionbar = config.getBoolean("general.prevent-building.show-actionbar", true);
        this.teleportPlayerDownwards = config.getBoolean("general.prevent-building.teleport-down-on-blockplace", false);
    }

    @Override
    public String name() {
        return "prevent-building";
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
        return NetherCeiling.getConfiguration().getBoolean("general.prevent-building.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!block.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (block.getLocation().getY() < 127) return;
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;

        event.setCancelled(true);
        if (teleportPlayerDownwards) {
            teleportFromCeiling(player);
            if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.locale()).general_cant_be_on_ceiling)
            ));
        } else {
            if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.locale()).building_disabled_on_ceiling)
            ));
        }
    }
}
