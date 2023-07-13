package me.xginko.netherceiling.modules.building;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LimitSpecificBlocks implements NetherCeilingModule, Listener {

    private final HashMap<Material, Integer> blockLimits = new HashMap<>();
    private final boolean showActionbar;
    private final int ceilingY;

    public LimitSpecificBlocks() {
        shouldEnable();
        Logger logger = NetherCeiling.getLog();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("building.limit-specific-blocks.enable", "Acts like a chunk limit, except it will only count blocks above the nether ceiling.");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("SOUL_SAND", 25);
        defaults.put("OBSIDIAN", 10);
        ConfigSection section = config.getConfigSection("building.limit-specific-blocks.blocks", defaults);
        for (String configuredMaterial : section.getKeys(false)) {
            try {
                Material limitedMaterial = Material.valueOf(configuredMaterial);
                Integer maxAmountPerChunk = Integer.valueOf(section.getString(configuredMaterial));
                this.blockLimits.put(limitedMaterial, maxAmountPerChunk);
            } catch (NumberFormatException e) {
                LogUtils.integerNotRecognized(Level.WARNING, name(), configuredMaterial);
            } catch (IllegalArgumentException e) {
                LogUtils.materialNotRecognized(Level.WARNING, name(), configuredMaterial);
            }
        }
        this.showActionbar = config.getBoolean("building.blacklist-specific-blocks.show-actionbar", true);
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "limit-specific-blocks";
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
        return NetherCeiling.getConfiguration().getBoolean("building.limit-specific-blocks.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        Block blockPlayerWantsToPlace = event.getBlock();
        if (!blockPlayerWantsToPlace.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (blockPlayerWantsToPlace.getY() < ceilingY) return;

        final Material materialPlayerWantsToPlace = blockPlayerWantsToPlace.getType();

        if (blockLimits.containsKey(materialPlayerWantsToPlace)) {
            Player player = event.getPlayer();
            if (player.hasPermission("netherceiling.bypass")) return;

            final Integer maxAllowedAmountOfLimitedMaterial = blockLimits.get(materialPlayerWantsToPlace);
            if (containsMoreBlocksThanAllowed(blockPlayerWantsToPlace.getChunk(), materialPlayerWantsToPlace)) {
                event.setCancelled(true);
                if (showActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.locale()).building_block_limit_reached
                                .replaceText(TextReplacementConfig.builder().matchLiteral("%amount%").replacement(maxAllowedAmountOfLimitedMaterial.toString()).build())
                                .replaceText(TextReplacementConfig.builder().matchLiteral("%block%").replacement(materialPlayerWantsToPlace.name()).build())
                );
            }
        }
    }

    private boolean containsMoreBlocksThanAllowed(Chunk chunk, Material limitedMaterial) {
        int count = 0;
        int maxY = chunk.getWorld().getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = ceilingY + 1; y < maxY; y++) {
                    if (chunk.getBlock(x, y, z).getType().equals(limitedMaterial)) {
                        count++;
                    }
                    if (count > blockLimits.get(limitedMaterial)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
