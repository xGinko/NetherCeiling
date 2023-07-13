package me.xginko.netherceiling.commands.netherceiling.subcommands;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

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
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("netherceiling.cmd.version")) {
            PluginDescriptionFile pluginyml = NetherCeiling.getInstance().getDescription();
            sender.sendMessage("\n");
            sender.sendMessage(
                    ChatColor.GOLD+pluginyml.getName()+" "+pluginyml.getVersion()+
                            ChatColor.GRAY+" by "+ChatColor.DARK_AQUA+pluginyml.getAuthors().get(0)
            );
            sender.sendMessage("\n");
        } else {
            sender.sendMessage(NetherCeiling.getLang(sender).noPermission);
        }
    }
}
