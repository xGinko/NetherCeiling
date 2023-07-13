package me.xginko.netherceiling.modules.vehicles;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import me.xginko.netherceiling.utils.LogUtils;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class PreventUsingSpecificVehicles implements NetherCeilingModule, Listener {

    private final HashSet<EntityType> blacklistedVehicles = new HashSet<>();
    private final boolean shouldShowActionbar, useAsWhitelist;
    private final int ceilingY;

    public PreventUsingSpecificVehicles() {
        shouldEnable();
        Config config = NetherCeiling.getConfiguration();
        config.addComment("vehicles.prevent-using-specific-vehicles.enable", "Will also eject players from their vehicles if they mounted it below and then go on top.");
        this.shouldShowActionbar = config.getBoolean("vehicles.prevent-using-specific-vehicles.show-actionbar", true);
        this.useAsWhitelist = config.getBoolean("vehicles.prevent-using-specific-vehicles.use-as-whitelist-instead", false);
        List<String> configuredVehicles = config.getList("vehicles.prevent-using-specific-vehicles.vehicles", Arrays.asList("BOAT", "HORSE", "DONKEY", "MULE"));
        for (String vehicleEntry : configuredVehicles) {
            try {
                EntityType vehicle = EntityType.valueOf(vehicleEntry);
                blacklistedVehicles.add(vehicle);
            } catch (IllegalArgumentException e) {
                LogUtils.entityTypeNotRecognized(Level.WARNING, name(), vehicleEntry);
            }
        }
        this.ceilingY = config.nether_ceiling_y;
    }

    @Override
    public String name() {
        return "prevent-using-specific-vehicles";
    }

    @Override
    public String category() {
        return "vehicles";
    }

    @Override
    public void enable() {
        NetherCeiling plugin = NetherCeiling.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NetherCeiling.getConfiguration().getBoolean("vehicles.prevent-using-specific-vehicles.enable", false) && !blacklistedVehicles.isEmpty();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void leaveVehiclesOnCeilingEnter(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;
        if (!(player.getVehicle() instanceof Vehicle)) return;
        if (player.hasPermission("netherceiling.bypass")) return;

        final EntityType vehicleType = player.getVehicle().getType();

        if (useAsWhitelist) {
            if (!blacklistedVehicles.contains(vehicleType)) {
                player.leaveVehicle();
                player.eject();
                if (shouldShowActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.getLocale()).vehicles_cant_ride_this_on_ceiling
                        .replace("%vehicle%", vehicleType.name())
                );
            }
        } else {
            if (blacklistedVehicles.contains(vehicleType)) {
                player.leaveVehicle();
                player.eject();
                if (shouldShowActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.getLocale()).vehicles_cant_ride_this_on_ceiling
                        .replace("%vehicle%", vehicleType.name())
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void blockVehicleEnterOnCeiling(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        Player player = (Player) event.getEntered();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;
        if (player.hasPermission("netherceiling.bypass")) return;

        EntityType vehicle = event.getVehicle().getType();

        if (useAsWhitelist) {
            if (!blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.getLocale()).vehicles_cant_ride_this_on_ceiling
                        .replace("%vehicle%", vehicle.name())
                );
            }
        } else {
            if (blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.getLocale()).vehicles_cant_ride_this_on_ceiling
                        .replace("%vehicle%", vehicle.name())
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyEntityMountOnCeiling(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < ceilingY) return;
        if (player.hasPermission("netherceiling.bypass")) return;

        EntityType vehicle = event.getMount().getType();

        if (useAsWhitelist) {
            if (!blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.getLocale()).vehicles_cant_ride_this_on_ceiling
                        .replace("%vehicle%", vehicle.name())
                );
            }
        } else {
            if (blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(
                        NetherCeiling.getLang(player.getLocale()).vehicles_cant_ride_this_on_ceiling
                        .replace("%vehicle%", vehicle.name())
                );
            }
        }
    }
}
