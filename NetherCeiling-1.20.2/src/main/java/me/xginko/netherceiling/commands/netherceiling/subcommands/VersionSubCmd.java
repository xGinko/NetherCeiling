package me.xginko.netherceiling.commands.netherceiling.subcommands;

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VersionSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "version";
    }
    @Override
    public TextComponent getDescription() {
        return Component.text("Show the plugin version.").color(NamedTextColor.GRAY);
    }
    @Override
    public TextComponent getSyntax() {
        return Component.text("/netherceiling version").color(NamedTextColor.GOLD);
    }

    @Override
    public void perform(@NotNull CommandSender sender, String[] args) {
        if (sender.hasPermission("netherceiling.cmd.version")) {
            PluginMeta pluginMeta = NetherCeiling.getInstance().getPluginMeta();
            sender.sendMessage(
                    Component.newline()
                    .append(Component.text(pluginMeta.getName()+" "+pluginMeta.getVersion()).color(NamedTextColor.GOLD)
                    .append(Component.text(" by ").color(NamedTextColor.GRAY))
                    .append(Component.text(pluginMeta.getAuthors().get(0)).color(NamedTextColor.DARK_AQUA))
                    .clickEvent(ClickEvent.openUrl("https://github.com/xGinko/NetherCeiling/")))
                    .append(Component.newline())
            );
        } else {
            sender.sendMessage(NetherCeiling.getLang(sender).no_permission);
        }
    }
}
