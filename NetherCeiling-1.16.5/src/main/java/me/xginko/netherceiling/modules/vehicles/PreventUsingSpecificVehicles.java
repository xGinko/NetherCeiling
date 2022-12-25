package me.xginko.netherceiling.modules.vehicles;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.config.Config;
import me.xginko.netherceiling.modules.NetherCeilingModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
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

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class PreventUsingSpecificVehicles implements NetherCeilingModule, Listener {

    private final HashSet<EntityType> blacklistedVehicles = new HashSet<>();
    private final boolean shouldShowActionbar, useAsWhitelist;

    public PreventUsingSpecificVehicles() {
        Config config = NetherCeiling.getConfiguration();
        this.shouldShowActionbar = config.getBoolean("vehicles.prevent-using-specific-vehicles.show-actionbar", true);
        this.useAsWhitelist = config.getBoolean("vehicles.prevent-using-specific-vehicles.use-as-whitelist-instead", false);
        List<String> configuredVehicles = config.getList("vehicles.prevent-using-specific-vehicles.vehicles", List.of("BOAT", "HORSE", "DONKEY", "MULE"));
        Logger logger = NetherCeiling.getLog();
        for (String vehicleEntry : configuredVehicles) {
            try {
                EntityType vehicle = EntityType.valueOf(vehicleEntry);
                blacklistedVehicles.add(vehicle);
            } catch (IllegalArgumentException e) {
                logger.warning("("+name()+") EntityType '"+vehicleEntry+"' not recognized! Please use correct values from https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html");
            }
        }
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
        if (blacklistedVehicles.isEmpty()) return false;
        return NetherCeiling.getConfiguration().getBoolean("vehicles.prevent-using-specific-vehicles.enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void leaveVehiclesOnCeilingEnter(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < 127) return;
        if (!(player.getVehicle() instanceof Vehicle vehicle)) return;

        EntityType vehicleType = vehicle.getType();

        if (useAsWhitelist) {
            if (!blacklistedVehicles.contains(vehicleType)) {
                player.leaveVehicle();
                player.eject();
                if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.locale()).vehicles_cant_ride_this_on_ceiling)
                        .replace("%vehicle%", vehicleType.name())
                ));
            }
        } else {
            if (blacklistedVehicles.contains(vehicleType)) {
                player.leaveVehicle();
                player.eject();
                if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.locale()).vehicles_cant_ride_this_on_ceiling)
                        .replace("%vehicle%", vehicleType.name())
                ));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void blockVehicleEnterOnCeiling(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < 127) return;

        EntityType vehicle = event.getVehicle().getType();

        if (useAsWhitelist) {
            if (!blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.locale()).vehicles_cant_ride_this_on_ceiling)
                        .replace("%vehicle%", vehicle.name())
                ));
            }
        } else {
            if (blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.locale()).vehicles_cant_ride_this_on_ceiling)
                        .replace("%vehicle%", vehicle.name())
                ));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void denyEntityMountOnCeiling(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.hasPermission("netherceiling.bypass")) return;
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (player.getLocation().getY() < 127) return;

        EntityType vehicle = event.getMount().getType();

        if (useAsWhitelist) {
            if (!blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.locale()).vehicles_cant_ride_this_on_ceiling)
                        .replace("%vehicle%", vehicle.name())
                ));
            }
        } else {
            if (blacklistedVehicles.contains(vehicle)) {
                event.setCancelled(true);
                if (shouldShowActionbar) player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&',
                        NetherCeiling.getLang(player.locale()).vehicles_cant_ride_this_on_ceiling)
                        .replace("%vehicle%", vehicle.name())
                ));
            }
        }
    }
}
