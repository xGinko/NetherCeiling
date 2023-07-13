package me.xginko.netherceiling.modules.fastblocks;

import io.papermc.paper.event.entity.EntityMoveEvent;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class VehicleOnFastBlocks implements NetherCeilingModule, Listener {

    private static final double tolerance = 0.2;
    private final HashSet<Material> fastBlocks = new HashSet<>();
    private final HashSet<EntityType> speedLimitedVehicles = new HashSet<>();
    private final double maxSpeed;
    private final boolean shouldShowActionbar;
    private final int ceilingY;

    public VehicleOnFastBlocks() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        this.maxSpeed = config.getDouble("fast-blocks.vehicle-speed.max-speed-in-bps", 15.5);
        this.shouldShowActionbar = config.getBoolean("fast-blocks.vehicle-speed.show-actionbar", true);
        List<String> configuredFastBlocks = config.getList("fast-blocks.vehicle-speed.fast-blocks", List.of("SOUL_SOIL", "SOUL_SAND", "BLUE_ICE", "PACKED_ICE", "ICE"));
        for (String configuredMaterial : configuredFastBlocks) {
            try {
                Material fastBlockMaterial = Material.valueOf(configuredMaterial);
                this.fastBlocks.add(fastBlockMaterial);
            } catch (IllegalArgumentException e) {
                LogUtils.materialNotRecognized(Level.WARNING, name(), configuredMaterial);
            }
        }
        List<String> configuredVehicles = config.getList("fast-blocks.vehicle-speed.vehicles", List.of("BOAT", "CHEST_BOAT"));
        for (String configuredVehicle : configuredVehicles) {
            try {
                EntityType disabledEntity = EntityType.valueOf(configuredVehicle);
                this.speedLimitedVehicles.add(disabledEntity);
            } catch (IllegalArgumentException e) {
                LogUtils.moduleLog(Level.WARNING, name(), configuredVehicle);
            }
        }
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "vehicle-speed";
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
        return NetherCeiling.getConfiguration().getBoolean("fast-blocks.vehicle-speed.enable", true);
    }

    @EventHandler(priority = EventPriority.NORMAL,ignoreCancelled = true)
    private void onEntityMove(EntityMoveEvent event) {
        if (!speedLimitedVehicles.contains(event.getEntityType())) return;
        if (!(event.getEntity() instanceof Vehicle vehicle)) return;
        if (!vehicle.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (vehicle.getLocation().getY() < ceilingY) return;

        Block blockAtEntityLegs = vehicle.getLocation().getBlock();
        Material materialEntityIsStandingIn = blockAtEntityLegs.getType();
        Material materialEntityIsStandingOn = blockAtEntityLegs.getRelative(BlockFace.DOWN).getType();

        if (fastBlocks.contains(materialEntityIsStandingOn)) {
            manageEntitySpeed(event.getFrom(), event.getTo(), materialEntityIsStandingOn, vehicle);
            return;
        }
        if (fastBlocks.contains(materialEntityIsStandingIn)) {
            manageEntitySpeed(event.getFrom(), event.getTo(), materialEntityIsStandingIn, vehicle);
        }
    }

    private void manageEntitySpeed(Location from, Location to, Material fastBlock, Vehicle vehicle) {
        if ((Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ()) * 20) > maxSpeed+tolerance) {
            vehicle.teleport(from);
            for (Entity passenger : vehicle.getPassengers()) {
                passenger.eject();
                passenger.leaveVehicle();
                if (shouldShowActionbar) {
                    if (passenger instanceof Player player) {
                        player.sendActionBar(
                                NetherCeiling.getLang(player.locale()).fastblocks_moving_on_block_is_limited
                                        .replaceText(TextReplacementConfig.builder().matchLiteral("%fastblock%").replacement(fastBlock.name()).build())
                        );
                    }
                }
            }
        }
    }
}