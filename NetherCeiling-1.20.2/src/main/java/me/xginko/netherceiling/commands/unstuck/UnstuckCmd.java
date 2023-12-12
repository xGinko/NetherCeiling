package me.xginko.netherceiling.commands.unstuck;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.NetherCeilingCommand;
import me.xginko.netherceiling.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static me.xginko.netherceiling.utils.CeilingUtils.teleportFromCeiling;

public class UnstuckCmd implements NetherCeilingCommand, Listener  {

    private final HashMap<UUID, ScheduledTask> teleportWarmups = new HashMap<>();
    private final boolean warmup_is_enabled;
    private final int nether_ceiling_y, warmup_delay_in_ticks;

    public UnstuckCmd() {
        Config config = NetherCeiling.getConfiguration();
        this.warmup_is_enabled = config.warmup_is_enabled;
        this.nether_ceiling_y = config.nether_ceiling_y;
        this.warmup_delay_in_ticks = config.warmup_delay_in_ticks;

        if (warmup_is_enabled && config.warmup_cancel_on_move_or_dmg) {
            NetherCeiling plugin = NetherCeiling.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @Override
    public String label() {
        return "unstuck";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command.").color(NamedTextColor.RED));
            return true;
        }

        if (player.hasPermission("netherceiling.cmd.unstuck")) {
            if (
                    player.getWorld().getEnvironment().equals(World.Environment.NETHER)
                    && player.getLocation().getY() > nether_ceiling_y
            ) {
                if (warmup_is_enabled) startTeleportWarmup(player);
                else teleportFromCeiling(player);
            } else {
                player.sendMessage(NetherCeiling.getLang(player.locale()).youre_not_on_the_ceiling);
            }
        } else {
            player.sendMessage(NetherCeiling.getLang(player.locale()).no_permission);
        }

        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onMoveDuringWarmup(PlayerMoveEvent event) {
        final UUID playerUniqueId = event.getPlayer().getUniqueId();
        if (
                teleportWarmups.containsKey(playerUniqueId)
                && !event.getTo().getBlock().getLocation().equals(event.getFrom().getBlock().getLocation())
        ){
            cancelWarmup(playerUniqueId, true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerTakesDamageDuringWarmup(EntityDamageEvent event) {
        cancelWarmup(event.getEntity().getUniqueId(), true);
    }

    private void startTeleportWarmup(Player player) {
        final UUID playerUniqueId = player.getUniqueId();
        ScheduledTask existingTask = teleportWarmups.get(playerUniqueId);
        if (existingTask != null) existingTask.cancel();

        Runnable newTask = new Runnable() {
            int timeLeft = warmup_delay_in_ticks / 20;
            @Override
            public void run() {
                if (timeLeft > 0) {
                    player.showTitle(Title.title(
                            NetherCeiling.getLang(player.locale()).teleport_commencing_in
                                    .replaceText(TextReplacementConfig.builder().matchLiteral("%seconds%").replacement(String.valueOf(timeLeft)).build()),
                            NetherCeiling.getLang(player.locale()).teleport_dont_move,
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO)
                    ));
                    timeLeft--;
                } else {
                    teleportFromCeiling(player);
                    cancelWarmup(playerUniqueId, false);
                }
            }
        };

        teleportWarmups.put(
                playerUniqueId,
                player.getScheduler().runAtFixedRate(NetherCeiling.getInstance(), warmupTask -> newTask.run(), null, 1L, 20L)
        );
    }

    private void cancelWarmup(UUID playerUniqueId, boolean sendMessage) {
        if (teleportWarmups.containsKey(playerUniqueId)) {
            teleportWarmups.get(playerUniqueId).cancel();
            teleportWarmups.remove(playerUniqueId);
            if (sendMessage) Optional.ofNullable(Bukkit.getPlayer(playerUniqueId)).ifPresent(player ->
                    player.sendMessage(NetherCeiling.getLang(player.locale()).teleport_cancelled)
            );
        }
    }
}
