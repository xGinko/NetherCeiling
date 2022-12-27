package me.xginko.netherceiling.modules.fastblocks;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
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
import java.util.logging.Logger;

public class PlayerOnFastBlocks implements NetherCeilingModule, Listener {

    private static final double tolerance = 0.4;
    private final HashSet<Material> fastBlocks = new HashSet<>();
    private final double maxSpeed;
    private final boolean shouldShowActionbar;

    public PlayerOnFastBlocks() {
        Config config = NetherCeiling.getConfiguration();
        this.maxSpeed = config.getDouble("fast-blocks.player-speed.max-speed-in-bps", 7.1);
        this.shouldShowActionbar = config.getBoolean("fast-blocks.player-speed.show-actionbar", true);
        List<String> configuredFastBlocks = config.getList("fast-blocks.player-speed.fast-blocks", List.of("SOUL_SOIL", "SOUL_SAND", "BLUE_ICE", "PACKED_ICE", "ICE"));
        Logger logger = NetherCeiling.getLog();
        for (String configuredFastBlock : configuredFastBlocks) {
            Material fastBlock = Material.getMaterial(configuredFastBlock);
            if (fastBlock != null) {
                fastBlocks.add(fastBlock);
            } else {
                logger.warning("("+name()+") Material '"+configuredFastBlock+"' not recognized! Please use correct values from https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
            }
        }
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
        if (player.getLocation().getY() < 127) return;

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
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        double distX = to.getX() - from.getX();
        double distZ = to.getZ() - from.getZ();
        double blocksPerSecond = Math.hypot(distX, distZ) * 20;

        if (blocksPerSecond > maxSpeed+tolerance) {
            event.setCancelled(true);
            if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                    NetherCeiling.getLang(player.locale()).fastblocks_moving_on_block_is_limited)
                    .replace("%fastblock%", fastBlock.name())
            ));
        }
    }
}
