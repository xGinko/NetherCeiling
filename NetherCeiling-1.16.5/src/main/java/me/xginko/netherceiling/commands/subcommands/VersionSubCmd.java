package me.xginko.netherceiling.commands.subcommands;

import org.jetbrains.annotations.NotNull;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class VersionSubCmd extends SubCommand {

    @Override
    public String getName() {
        return "version";
    }
    @Override
    public String getDescription() {
        return "Show the plugin version.";
    }
    @Override
    public String getSyntax() {
        return "/netherceiling version";
    }

    @Override
    public void perform(@NotNull CommandSender sender, String[] args) {
        if (sender.hasPermission("netherceiling.cmd.version")) {
            sender.sendMessage("\n");
            sender.sendMessage(
                    ChatColor.GOLD+"NetherCeiling v"+ NetherCeiling.getInstance().getDescription().getVersion()+ ChatColor.GRAY+" by "+ChatColor.DARK_AQUA+"xGinko"
            );
            sender.sendMessage("\n");
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NetherCeiling.getLang(sender).noPermission));
        }
    }
}
