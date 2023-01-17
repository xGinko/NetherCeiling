package me.xginko.netherceiling.commands;

import me.xginko.netherceiling.NetherCeiling;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.xginko.netherceiling.utils.CeilingUtils.teleportFromCeiling;

public class UnstuckCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("unstuck")) {
            if (sender instanceof Player player) {
                if (player.hasPermission("netherceiling.cmd.unstuck")) {
                    if (
                            player.getWorld().getEnvironment().equals(World.Environment.NETHER)
                            && player.getLocation().getY() > NetherCeiling.getConfiguration().nether_ceiling_y
                    ) {
                        teleportFromCeiling(player);
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', NetherCeiling.getLang(player.locale()).youre_not_on_the_ceiling));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', NetherCeiling.getLang(player.locale()).noPermission));
                }
            } else {
                sender.sendMessage(ChatColor.RED+"Only players can execute this command.");
            }
        }
        return true;
    }
}
