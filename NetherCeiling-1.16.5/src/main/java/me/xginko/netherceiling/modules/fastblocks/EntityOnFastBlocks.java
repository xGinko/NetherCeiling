package me.xginko.netherceiling.modules.fastblocks;

import io.papermc.paper.event.entity.EntityMoveEvent;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class EntityOnFastBlocks implements NetherCeilingModule, Listener {

    private static final double tolerance = 0.2;
    private final HashSet<Material> fastBlocks = new HashSet<>();
    private final HashSet<EntityType> speedLimitedEntities = new HashSet<>();
    private final double maxSpeed;
    private final boolean shouldShowActionbar;

    public EntityOnFastBlocks() {
        Config config = NetherCeiling.getConfiguration();
        Logger logger = NetherCeiling.getLog();
        this.maxSpeed = config.getDouble("fast-blocks.entity-speed.max-speed-in-bps", 15.5);
        this.shouldShowActionbar = config.getBoolean("fast-blocks.entity-speed.show-actionbar-to-nearby-players", true);
        List<String> configuredFastBlocks = config.getList("fast-blocks.entity-speed.fast-blocks", List.of("SOUL_SOIL", "SOUL_SAND", "BLUE_ICE", "PACKED_ICE", "ICE"));
        for (String configuredFastBlock : configuredFastBlocks) {
            Material fastBlock = Material.getMaterial(configuredFastBlock);
            if (fastBlock != null) {
                fastBlocks.add(fastBlock);
            } else {
                logger.warning("("+name()+") Material '"+configuredFastBlock+"' not recognized! Please use correct values from https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
            }
        }
        List<String> configuredEntities = config.getList("fast-blocks.entity-speed.entities", List.of("BOAT", "CHEST_BOAT"));
        for (String configuredEntity : configuredEntities) {
            try {
                EntityType disabledEntity = EntityType.valueOf(configuredEntity);
                speedLimitedEntities.add(disabledEntity);
            } catch (IllegalArgumentException e) {
                logger.warning("("+name()+") EntityType '"+configuredEntity+"' not recognized! Please use correct values from https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html");
            }
        }
    }

    @Override
    public String name() {
        return "entity-speed";
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
        return NetherCeiling.getConfiguration().getBoolean("fast-blocks.entity-speed.enable", true);
    }

    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    public void onPlayerMove(EntityMoveEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        if (!speedLimitedEntities.contains(entity.getType())) return;
        if (!entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (entity.getLocation().getY() < 127) return;

        Block blockAtEntityLegs = entity.getLocation().getBlock();
        Material materialEntityIsStandingIn = blockAtEntityLegs.getType();
        Material materialEntityIsStandingOn = blockAtEntityLegs.getRelative(BlockFace.DOWN).getType();

        if (fastBlocks.contains(materialEntityIsStandingOn)) {
            manageEntitySpeed(event, materialEntityIsStandingOn);
            return;
        }
        if (fastBlocks.contains(materialEntityIsStandingIn)) {
            manageEntitySpeed(event, materialEntityIsStandingIn);
        }
    }

    private void manageEntitySpeed(EntityMoveEvent event, Material fastBlock) {
        Location from = event.getFrom();
        Location to = event.getTo();
        double distX = to.getX() - from.getX();
        double distZ = to.getZ() - from.getZ();
        double blocksPerSecond = Math.hypot(distX, distZ) * 20;

        if (blocksPerSecond > maxSpeed+tolerance) {
            event.setCancelled(true);
            if (shouldShowActionbar) {
                for (Player nearbyPlayer : event.getEntity().getLocation().getNearbyPlayers(3, 3, 3)) {
                    nearbyPlayer.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                            NetherCeiling.getLang(nearbyPlayer.locale()).fastblocks_moving_on_block_is_limited)
                            .replace("%fastblock%", fastBlock.name())
                    ));
                }
            }
        }
    }
}