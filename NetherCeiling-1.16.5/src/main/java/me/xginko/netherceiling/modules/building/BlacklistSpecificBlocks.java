package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
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
        List<String> configuredBlacklistedBlocks = config.getList("building.blacklist-specific-blocks.blocks", List.of(
                "SOUL_SAND", "SOUL_SOIL", "BLUE_ICE", "PACKED_ICE", "ICE"
        ));
        for (String configuredMaterial : configuredBlacklistedBlocks) {
            try {
                Material blacklistedMaterial = Material.valueOf(configuredMaterial);
                blacklistedBlocks.add(blacklistedMaterial);
            } catch (IllegalArgumentException e) {
                LogUtils.materialNotRecognized(Level.WARNING, name(), configuredMaterial);
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

        if (useAsWhitelist) {
            if (!blacklistedBlocks.contains(placedBlock.getType())) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                if (player.hasPermission("netherceiling.bypass")) return;
                if (showActionbar) player.sendActionBar(NetherCeiling.getLang(player.locale()).building_block_cant_be_placed
                        .replaceText(TextReplacementConfig.builder().matchLiteral("%block%").replacement(placedBlock.getType().name()).build())
                );
            }
        } else {
            if (blacklistedBlocks.contains(placedBlock.getType())) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                if (player.hasPermission("netherceiling.bypass")) return;
                if (showActionbar) player.sendActionBar(NetherCeiling.getLang(player.locale()).building_block_cant_be_placed
                        .replaceText(TextReplacementConfig.builder().matchLiteral("%block%").replacement(placedBlock.getType().name()).build())
                );
            }
        }
    }
}
