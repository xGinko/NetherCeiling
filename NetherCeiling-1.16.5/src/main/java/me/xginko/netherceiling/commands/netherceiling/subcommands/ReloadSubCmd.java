package me.xginko.netherceiling.commands.netherceiling.subcommands;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "reload";
    }
    @Override
    public TextComponent getDescription() {
        return Component.text("Reload the plugin configuration.").color(NamedTextColor.GRAY);
    }
    @Override
    public TextComponent getSyntax() {
        return Component.text("/netherceiling reload").color(NamedTextColor.GOLD);
    }

    @Override
    public void perform(@NotNull CommandSender sender, String[] args) {
        if (sender.hasPermission("netherceiling.cmd.reload")) {
            sender.sendMessage(Component.text("Reloading NetherCeiling...").color(NamedTextColor.WHITE));
            NetherCeiling plugin = NetherCeiling.getInstance();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.reloadPlugin();
                sender.sendMessage(Component.text("Reload complete.").color(NamedTextColor.GREEN));
            });
        } else {
            sender.sendMessage(NetherCeiling.getLang(sender).no_permission);
        }
    }
}
