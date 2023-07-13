package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnablePlacingWater implements NetherCeilingModule, Listener {

    private final boolean strikeLightning;
    private final int ceilingY;

    public EnablePlacingWater() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("building.enable-placing-water.enable", "Enables players to use water buckets on the ceiling. (Woah illegal water in the nether)");
        this.strikeLightning = config.getBoolean("building.enable-placing-water.strike-lightning-on-water-place", false);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "enable-placing-water";
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
        return NetherCeiling.getConfiguration().getBoolean("building.enable-placing-water.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NETHER)) return;

        ItemStack usedItem = event.getItem();
        if (usedItem == null || !usedItem.getType().equals(Material.WATER_BUCKET)) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Block selectedBlock = clickedBlock.getRelative(event.getBlockFace());
        if (selectedBlock.getY() >= ceilingY) {
            event.setCancelled(true);
            selectedBlock.setType(Material.WATER, true);
            if (strikeLightning) selectedBlock.getWorld().strikeLightning(selectedBlock.getLocation());
        }
    }
}
