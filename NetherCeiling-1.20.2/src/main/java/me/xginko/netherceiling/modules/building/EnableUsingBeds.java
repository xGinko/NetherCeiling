package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class EnableUsingBeds implements NetherCeilingModule, Listener {

    private final int ceilingY;

    public EnableUsingBeds() {
        Config config = NetherCeiling.getConfiguration();
        config.addComment("building.enable-using-beds", "Allows players on the ceiling to sleep in beds just like in the overworld.");
        this.ceilingY = config.nether_ceiling_y;
    }

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
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("building.enable-using-beds", true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;

        Block bed = event.getBed();
        if (bed.getY() < ceilingY) return;

        event.setUseBed(Event.Result.ALLOW);
        player.setBedSpawnLocation(bed.getLocation());
        player.sendMessage(NetherCeiling.getLang(player.locale()).building_bed_respawn_set);
    }
}
