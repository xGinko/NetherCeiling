package me.xginko.netherceiling.utils;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class CeilingUtils {

    public static void teleportFromCeiling(Player player) {
        Config config = NetherCeiling.getConfiguration();
        Location playerLocation = player.getLocation();

        // Teleport Player Downwards
        player.teleport(new Location(
                player.getWorld(), playerLocation.getBlockX(), config.nether_ceiling_y-config.teleport_distance_in_blocks, playerLocation.getBlockZ()
        ));

        if (config.safe_teleport_enabled || player.hasPermission("netherceiling.safeteleport")) {
            // Check block above for liquid or falling block
            Block blockAboveHead = playerLocation.getBlock().getRelative(BlockFace.UP, 2);
            if (blockAboveHead.isLiquid() || blockAboveHead.getType().hasGravity()) {
                blockAboveHead.setType(Material.NETHERRACK, false);
            }

            // Create air pocket for player
            Block blockAtPlayerLegs = playerLocation.getBlock();
            if (!blockAtPlayerLegs.getType().equals(Material.AIR)) {
                blockAtPlayerLegs.setType(Material.AIR, false);
            }
            Block blockAtPlayerTorso = blockAtPlayerLegs.getRelative(BlockFace.UP);
            if (!blockAtPlayerTorso.getType().equals(Material.AIR)) {
                blockAtPlayerTorso.setType(Material.AIR, false);
            }

            // Check all sides of air pocket for liquids and fill with netherrack
            for (int i = 0; i < 2; i++) {
                Block airPocketBlock = blockAtPlayerLegs.getRelative(BlockFace.UP, i);
                if (airPocketBlock.getRelative(BlockFace.NORTH).isLiquid())
                    airPocketBlock.getRelative(BlockFace.NORTH).setType(Material.NETHERRACK, false);
                if (airPocketBlock.getRelative(BlockFace.EAST).isLiquid())
                    airPocketBlock.getRelative(BlockFace.EAST).setType(Material.NETHERRACK, false);
                if (airPocketBlock.getRelative(BlockFace.SOUTH).isLiquid())
                    airPocketBlock.getRelative(BlockFace.SOUTH).setType(Material.NETHERRACK, false);
                if (airPocketBlock.getRelative(BlockFace.WEST).isLiquid())
                    airPocketBlock.getRelative(BlockFace.WEST).setType(Material.NETHERRACK, false);
            }

            // Create block below feet if not solid
            Block blockBelowFeet = blockAtPlayerLegs.getRelative(BlockFace.DOWN);
            if (!blockBelowFeet.isSolid()) {
                blockBelowFeet.setType(Material.NETHERRACK, false);
            }
        }
    }

}
