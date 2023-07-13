package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class CustomBuildHeight implements NetherCeilingModule, Listener {

    private final int buildHeight;
    private final boolean showActionbar;

    public CustomBuildHeight() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("building.custom-build-height.enable", "Set a custom building height above the ceiling.");
        this.buildHeight = config.getInt("building.custom-build-height.height", 320);
        this.showActionbar = config.getBoolean("building.blacklist-specific-blocks.show-actionbar", true);
    }

    @Override
    public String name() {
        return "custom-build-height";
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
        return NetherCeiling.getConfiguration().getBoolean("building.custom-build-height.enable", true);
    }

    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    private void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!block.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (block.getLocation().getY() < buildHeight) return;

        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;

        event.setCancelled(true);
        if (showActionbar) player.sendActionBar(
                NetherCeiling.getLang(player.locale()).building_build_height_is_at
                        .replaceText(TextReplacementConfig.builder().matchLiteral("%buildheight%").replacement(String.valueOf(buildHeight)).build())
        );
    }
}
