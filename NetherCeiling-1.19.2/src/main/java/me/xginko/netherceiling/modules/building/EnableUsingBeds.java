package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class EnableUsingBeds implements NetherCeilingModule, Listener {

    public EnableUsingBeds() {}

    @Override
    public String name() {
        return "null";
    }

    @Override
    public String category() {
        return "null";
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
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;

        Block bed = event.getBed();
        if (bed.getY() < 127) return;

        event.setUseBed(Event.Result.ALLOW);
        player.setBedSpawnLocation(bed.getLocation());
        player.sendMessage(Component.text(
                ChatColor.translateAlternateColorCodes('&', NetherCeiling.getLang(player.locale()).building_bed_respawn_set
        )));
    }
}
