package me.xginko.netherceiling.commands.netherceiling;

import me.xginko.netherceiling.commands.NetherCeilingCommand;
import me.xginko.netherceiling.commands.SubCommand;
import me.xginko.netherceiling.commands.netherceiling.subcommands.ReloadSubCmd;
import me.xginko.netherceiling.commands.netherceiling.subcommands.VersionSubCmd;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetherCeilingCmd implements NetherCeilingCommand, TabCompleter {

    private final List<SubCommand> subcommands = new ArrayList<>();
    private final List<String> tabCompletes = new ArrayList<>();

    public NetherCeilingCmd() {
        subcommands.add(new ReloadSubCmd());
        subcommands.add(new VersionSubCmd());
        for (SubCommand subcommand : subcommands) {
            tabCompletes.add(subcommand.getLabel());
        }
    }

    @Override
    public String label() {
        return "netherceiling";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return args.length == 1 ? tabCompletes : null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            boolean cmdExists = false;
            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getLabel())) {
                    subcommand.perform(sender, args);
                    cmdExists = true;
                }
            }
            if (!cmdExists) showCommandOverviewTo(sender);
        } else {
            showCommandOverviewTo(sender);
        }
        return true;
    }

    private void showCommandOverviewTo(CommandSender sender) {
        if (!sender.hasPermission("netherceiling.cmd.*")) return;
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("NetherCeiling Commands").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        for (SubCommand subCommand : subcommands) {
            sender.sendMessage(
                    subCommand.getSyntax()
                    .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                    .append(subCommand.getDescription())
            );
        }
        sender.sendMessage(
                Component.text("/unstuck").color(NamedTextColor.GOLD)
                .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("Teleport yourself down from the nether ceiling.").color(NamedTextColor.GRAY))
        );
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
    }
}
