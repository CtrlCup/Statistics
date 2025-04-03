package eu.gamerfreak.statistics.commands;

import com.sun.tools.javac.Main;
import eu.gamerfreak.statistics.Statistics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Info implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] arg) {
        String version = Statistics.getInstance().getDescription().getVersion();
        if(sender.hasPermission("statistics.statistics")) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.GREEN + "Statistics Version: " + ChatColor.GOLD + version +
                        ChatColor.GRAY + "\nUse the permission " + ChatColor.RED + "Statistics.user" +
                        ChatColor.GRAY + " for the normal users");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Statistics Version: " + ChatColor.GOLD + version);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Nope :3");
        return true;
    }
}
