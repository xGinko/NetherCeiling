package me.xginko.netherceiling.commands.netherceiling.subcommands;

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
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("netherceiling.cmd.reload")) {
            sender.sendMessage(ChatColor.WHITE + "Reloading NetherCeiling config...");
            NetherCeiling plugin = NetherCeiling.getInstance();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.reloadPlugin();
                sender.sendMessage(ChatColor.GREEN + "Reload complete.");
            });
        } else {
            sender.sendMessage(NetherCeiling.getLang(sender).noPermission);
        }
    }
}
