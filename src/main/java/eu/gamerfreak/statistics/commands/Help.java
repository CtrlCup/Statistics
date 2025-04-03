package eu.gamerfreak.statistics.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Help implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("statistics.hide")) {
            return true;
        }
        commandSender.sendMessage(ChatColor.YELLOW + "/Help" + ChatColor.WHITE + " - List of all Commands");
        commandSender.sendMessage(ChatColor.YELLOW + "/Info" + ChatColor.WHITE + " - Info about the Plugin");
        return true;
    }
}
