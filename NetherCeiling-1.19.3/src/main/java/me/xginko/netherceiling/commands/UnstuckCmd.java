package me.xginko.netherceiling.commands;

import me.xginko.netherceiling.NetherCeiling;
import org.bukkit.ChatColor;
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
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED+"Only players can execute this command.");
                return true;
            }
            if (sender.hasPermission("netherceiling.cmd.unstuck")) {
                Player player = (Player) sender;
                teleportFromCeiling(player);
            } else {
                sender.sendMessage(NetherCeiling.getLang(sender).noPermission);
            }
        }
        return true;
    }
}
