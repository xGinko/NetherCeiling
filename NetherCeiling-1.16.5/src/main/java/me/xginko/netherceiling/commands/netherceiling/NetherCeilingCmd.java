package me.xginko.netherceiling.commands.netherceiling;

import me.xginko.netherceiling.commands.NetherCeilingCommand;
import me.xginko.netherceiling.commands.SubCommand;
import me.xginko.netherceiling.commands.netherceiling.subcommands.ReloadSubCmd;
import me.xginko.netherceiling.commands.netherceiling.subcommands.VersionSubCmd;
import org.bukkit.ChatColor;
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
            tabCompletes.add(subcommand.getName());
        }
    }

    @Override
    public String label() {
        return "netherceiling";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return tabCompletes;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            boolean cmdExists = false;
            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
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
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        sender.sendMessage(ChatColor.WHITE+"NetherCeiling Commands ");
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        sender.sendMessage(ChatColor.WHITE+"/unstuck"+ChatColor.DARK_GRAY+" - "+ChatColor.GRAY+"Teleport yourself down from the nether ceiling.");
        for (SubCommand subcommand : subcommands) {
            sender.sendMessage(
                    ChatColor.WHITE + subcommand.getSyntax()
                            + ChatColor.DARK_GRAY + " - "
                            + ChatColor.GRAY + subcommand.getDescription()
            );
        }
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
    }
}
