package me.xginko.netherceiling.commands.netherceiling.subcommands;

import org.jetbrains.annotations.NotNull;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }
    @Override
    public String getDescription() {
        return "Reload the plugin configuration.";
    }
    @Override
    public String getSyntax() {
        return "/netherceiling reload";
    }

    @Override
    public void perform(@NotNull CommandSender sender, String[] args) {
        if (sender.hasPermission("netherceiling.cmd.reload")) {
            sender.sendMessage(ChatColor.RED + "Reloading NetherCeiling config...");
            NetherCeiling.getInstance().reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Reload complete.");
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NetherCeiling.getLang(sender).noPermission));
        }
    }
}
