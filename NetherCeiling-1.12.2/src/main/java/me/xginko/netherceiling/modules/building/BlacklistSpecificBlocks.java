package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class BlacklistSpecificBlocks implements NetherCeilingModule, Listener {

    private final HashSet<Material> blacklistedBlocks = new HashSet<>();
    private final boolean useAsWhitelist, showActionbar;
    private final int ceilingY;

    public BlacklistSpecificBlocks() {
        shouldEnable();
        Logger logger = NetherCeiling.getInstance().getLogger();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("building.blacklist-specific-blocks.enable", "Prevent players from placing blocks of specific type above the ceiling.");
        List<String> configuredBlacklistedBlocks = config.getList("building.blacklist-specific-blocks", Arrays.asList(
                "SOUL_SAND", "SOUL_SOIL", "BLUE_ICE", "PACKED_ICE", "ICE"
        ));
        for (String configuredBlock : configuredBlacklistedBlocks) {
            Material blacklistedMaterial = Material.getMaterial(configuredBlock);
            if (blacklistedMaterial != null) {
                blacklistedBlocks.add(blacklistedMaterial);
            } else {
                logger.warning("("+name()+") Material '"+configuredBlock+"' not recognized! Please use correct values from https://helpch.at/docs/1.12.2/org/bukkit/Material.html");
            }
        }
        this.showActionbar = config.getBoolean("building.blacklist-specific-blocks.show-actionbar", true);
        this.useAsWhitelist = config.getBoolean("building.blacklist-specific-blocks.use-as-whitelist-instead", false);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "blacklist-specific-materials";
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
        return NetherCeiling.getConfiguration().getBoolean("building.blacklist-specific-blocks.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        if (!placedBlock.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (placedBlock.getLocation().getY() < ceilingY) return;

        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;

        if (useAsWhitelist) {
            if (!blacklistedBlocks.contains(placedBlock.getType())) {
                event.setCancelled(true);
                if (showActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.getLocale()).building_block_cant_be_placed)
                        .replace("%block%", placedBlock.getType().name())
                );
            }
        } else {
            if (blacklistedBlocks.contains(placedBlock.getType())) {
                event.setCancelled(true);
                if (showActionbar) player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.getLocale()).building_block_cant_be_placed)
                        .replace("%block%", placedBlock.getType().name())
                );
            }
        }
    }
}
