package me.xginko.netherceiling.commands.unstuck;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.NetherCeilingCommand;
import me.xginko.netherceiling.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import static me.xginko.netherceiling.utils.CeilingUtils.teleportFromCeiling;

public class UnstuckCmd implements NetherCeilingCommand, Listener  {

    private final HashMap<UUID, BukkitTask> warmupTasks = new HashMap<>();
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;
        if (player.hasPermission("netherceiling.cmd.unstuck")) {
            if (
                    player.getWorld().getEnvironment().equals(World.Environment.NETHER)
                    && player.getLocation().getY() > nether_ceiling_y
            ) {
                if (warmup_is_enabled) startTeleportWarmup(player);
                else teleportFromCeiling(player);
            } else {
                player.sendMessage(NetherCeiling.getLang(player.getLocale()).youre_not_on_the_ceiling);
            }
        } else {
            player.sendMessage(NetherCeiling.getLang(player.getLocale()).noPermission);
        }

        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onMoveDuringWarmup(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (
                warmupTasks.containsKey(player.getUniqueId())
                && !event.getTo().getBlock().getLocation().equals(event.getFrom().getBlock().getLocation())
        ){
            cancelTeleport(player.getUniqueId());
            player.sendMessage(NetherCeiling.getLang(player.getLocale()).teleport_cancelled);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerTakesDamageDuringWarmup(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (warmupTasks.containsKey(player.getUniqueId())){
                cancelTeleport(player.getUniqueId());
                player.sendMessage(NetherCeiling.getLang(player.getLocale()).teleport_cancelled);
            }
        }
    }

    private void startTeleportWarmup(Player player) {
        UUID playerUniqueId = player.getUniqueId();
        BukkitTask existingTask = warmupTasks.get(playerUniqueId);
        if (existingTask != null) existingTask.cancel();
        BukkitTask newTask = new BukkitRunnable() {
            int timeLeft = warmup_delay_in_ticks / 20;
            @Override
            public void run() {
                if (timeLeft > 0) {
                    player.sendTitle(
                            NetherCeiling.getLang(player.getLocale()).teleport_commencing_in.replace("%seconds%", String.valueOf(timeLeft)),
                            NetherCeiling.getLang(player.getLocale()).teleport_dont_move,
                            0,30,0
                    );
                    timeLeft--;
                } else {
                    teleportFromCeiling(player);
                    cancelTeleport(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(NetherCeiling.getInstance(), 0L, 20L);
        warmupTasks.put(playerUniqueId, newTask);
    }

    private void cancelTeleport(UUID playerUniqueId) {
        BukkitTask existingTask = warmupTasks.get(playerUniqueId);
        if (existingTask != null) {
            existingTask.cancel();
            warmupTasks.remove(playerUniqueId);
        }
    }
}
