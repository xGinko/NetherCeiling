package me.xginko.netherceiling.commands.subcommands;

import com.sun.istack.internal.NotNull;
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
        if (sender.hasPermission("anarchyexploitfixes.cmd.reload")) {
            sender.sendMessage(ChatColor.WHITE + "Reloading NetherCeiling...");
            NetherCeiling plugin = NetherCeiling.getInstance();
            plugin.reloadLang();
            plugin.reloadNetherCeilingConfig();
            sender.sendMessage(ChatColor.GREEN + "Reload complete.");
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NetherCeiling.getLang(sender).noPermission));
        }
    }
}
