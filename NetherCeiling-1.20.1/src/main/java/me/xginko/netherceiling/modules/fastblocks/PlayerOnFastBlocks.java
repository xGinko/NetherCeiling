package me.xginko.netherceiling.modules.fastblocks;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class PlayerOnFastBlocks implements NetherCeilingModule, Listener {

    private static final double tolerance = 0.2;
    private final HashSet<Material> fastBlocks = new HashSet<>();
    private final double maxSpeed;
    private final boolean shouldShowActionbar;
    private final int ceilingY;

    public PlayerOnFastBlocks() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("fast-blocks.player-speed", "Fast blocks are blocks like Ice or Soul Sand, where an entity or player can move on\nand gain higher speeds.");
        this.maxSpeed = config.getDouble("fast-blocks.player-speed.max-speed-in-bps", 7.1);
        this.shouldShowActionbar = config.getBoolean("fast-blocks.player-speed.show-actionbar", true);
        List<String> configuredFastBlocks = config.getList("fast-blocks.player-speed.fast-blocks", List.of("SOUL_SOIL", "SOUL_SAND", "BLUE_ICE", "PACKED_ICE", "ICE"));
        for (String configuredMaterial : configuredFastBlocks) {
            try {
                Material fastBlockMaterial = Material.valueOf(configuredMaterial);
                this.fastBlocks.add(fastBlockMaterial);
            } catch (IllegalArgumentException e) {
                LogUtils.materialNotRecognized(Level.WARNING, name(), configuredMaterial);
            }
        }
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "player-speed";
    }

    @Override
    public String category() {
        return "fast-blocks";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("fast-blocks.player-speed.enable", true);
    }

    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isGliding()) return;
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;

        Block blockAtPlayerLegs = player.getLocation().getBlock();
        Material materialPlayerIsStandingIn = blockAtPlayerLegs.getType(); // for blocks players sink in
        Material materialPlayerIsStandingOn = blockAtPlayerLegs.getRelative(BlockFace.DOWN).getType();

        if (fastBlocks.contains(materialPlayerIsStandingOn)) {
            managePlayerSpeed(event, materialPlayerIsStandingOn);
            return;
        }
        if (fastBlocks.contains(materialPlayerIsStandingIn)) {
            managePlayerSpeed(event, materialPlayerIsStandingIn);
        }
    }

    private void managePlayerSpeed(PlayerMoveEvent event, Material fastBlock) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if ((Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ()) * 20) > maxSpeed+tolerance) {
            event.setCancelled(true);
            if (shouldShowActionbar) {
                Player player = event.getPlayer();
                player.sendActionBar(
                        NetherCeiling.getLang(player.locale()).fastblocks_moving_on_block_is_limited
                                .replaceText(TextReplacementConfig.builder().matchLiteral("%fastblock%").replacement(fastBlock.name()).build())
                );
            }
        }
    }
}
