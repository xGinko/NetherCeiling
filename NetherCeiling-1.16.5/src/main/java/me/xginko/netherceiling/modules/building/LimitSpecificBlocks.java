package me.xginko.netherceiling.modules.building;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LimitSpecificBlocks implements NetherCeilingModule, Listener {

    private final HashMap<Material, Integer> blockLimits = new HashMap<>();
    private final boolean showActionbar;

    public LimitSpecificBlocks() {
        Logger logger = NetherCeiling.getLog();
        Config config = NetherCeiling.getConfiguration();
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("SOUL_SAND", 25);
        defaults.put("OBSIDIAN", 10);
        ConfigurationSection section = config.getConfigSection("building.limit-specific-blocks.blocks", defaults);
        for (String configuredMaterial : section.getKeys(false)) {
            Integer maxAmountPerChunk = Integer.valueOf(section.getString(configuredMaterial));
            Material blockMaterial = Material.getMaterial(configuredMaterial);
            if (blockMaterial != null) {
                blockLimits.put(blockMaterial, maxAmountPerChunk);
            } else {
                logger.warning("("+name()+") Material '"+configuredMaterial+"' not recognized. Use correct Material enums from https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
            }
        }
        this.showActionbar = config.getBoolean("building.blacklist-specific-blocks.show-actionbar", true);
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
        if (blockPlayerWantsToPlace.getY() < 127) return;

        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        Material materialPlayerWantsToPlace = blockPlayerWantsToPlace.getType();

        for (Map.Entry<Material, Integer> entry : blockLimits.entrySet()) {
            Material limitedMaterial = entry.getKey();
            if (limitedMaterial.equals(materialPlayerWantsToPlace)) {
                Integer maxAllowedAmountOfLimitedMaterial = entry.getValue();
                if (amountOfMaterialInCeilingChunk(blockPlayerWantsToPlace.getChunk(), limitedMaterial) > maxAllowedAmountOfLimitedMaterial) {
                    event.setCancelled(true);
                    if (showActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                            NetherCeiling.getLang(player.locale()).building_block_limit_reached)
                            .replace("%amount%", String.valueOf(maxAllowedAmountOfLimitedMaterial))
                            .replace("%block%", limitedMaterial.name())
                    ));
                    break;
                }
            }
        }
    }

    public int amountOfMaterialInCeilingChunk(Chunk chunk, Material material) {
        int count = 0;
        int maxY = chunk.getWorld().getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 127; y < maxY; y++) {
                    if (chunk.getBlock(x, y, z).getType().equals(material)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
