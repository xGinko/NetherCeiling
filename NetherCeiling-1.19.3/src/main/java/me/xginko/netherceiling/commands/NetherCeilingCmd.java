package me.xginko.netherceiling.commands;

import me.xginko.netherceiling.commands.subcommands.ReloadSubCmd;
import me.xginko.netherceiling.commands.subcommands.VersionSubCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetherCeilingCmd implements CommandExecutor, TabCompleter {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final List<String> tabcompleters = new ArrayList<>();

    public NetherCeilingCmd() {
        subcommands.add(new ReloadSubCmd());
        subcommands.add(new VersionSubCmd());
        for (int i=0; i<getSubcommands().size(); i++) {
            tabcompleters.add(getSubcommands().get(i).getName());
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            return tabcompleters;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            boolean cmdExists = false;
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    getSubcommands().get(i).perform(sender, args);
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
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        sender.sendMessage(ChatColor.WHITE+"NetherCeiling Commands ");
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        sender.sendMessage(ChatColor.WHITE+"/unstuck"+ChatColor.DARK_GRAY+" - "+ChatColor.GRAY+"Teleport yourself down from the nether ceiling.");
        for (int i=0; i < getSubcommands().size(); i++) {
            sender.sendMessage(
                    ChatColor.WHITE+getSubcommands().get(i).getSyntax()
                            +ChatColor.DARK_GRAY+" - "
                            +ChatColor.GRAY+getSubcommands().get(i).getDescription()
            );
        }
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
    }

    public ArrayList<SubCommand> getSubcommands() { return subcommands; }
}
