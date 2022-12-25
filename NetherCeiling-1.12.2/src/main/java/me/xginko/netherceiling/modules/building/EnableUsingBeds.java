package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnableUsingBeds implements NetherCeilingModule, Listener {

    public EnableUsingBeds() {}

    @Override
    public String name() {
        return "enable-using-beds";
    }

    @Override
    public String category() {
        return "building";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("building.enable-using-beds", true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBedInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.getLocation().getY() < 127) return;

        if (clickedBlock.getState() instanceof Bed) {
            event.setCancelled(true);
            player.setBedSpawnLocation(clickedBlock.getLocation(), true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.getLocale()).building_bed_respawn_set)
            );
        }
    }
}
