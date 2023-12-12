package me.xginko.netherceiling.commands;

import me.xginko.netherceiling.NetherCeiling;
import me.xginko.netherceiling.commands.netherceiling.NetherCeilingCmd;
import me.xginko.netherceiling.commands.unstuck.UnstuckCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public interface NetherCeilingCommand extends CommandExecutor {

    String label();
    @Override
    boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);

    HashSet<NetherCeilingCommand> commands = new HashSet<>();
    static void reloadCommands() {
        commands.clear();

        commands.add(new NetherCeilingCmd());
        commands.add(new UnstuckCmd());

        NetherCeiling plugin = NetherCeiling.getInstance();
        CommandMap commandMap = plugin.getServer().getCommandMap();
        for (NetherCeilingCommand command : commands) {
            plugin.getCommand(command.label()).unregister(commandMap);
            plugin.getCommand(command.label()).setExecutor(command);
        }
    }

}
