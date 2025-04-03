package eu.gamerfreak.statistics;

import eu.gamerfreak.statistics.commands.Info;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Statistics extends JavaPlugin {

    private static Statistics instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getCommand("stats").setExecutor(new Info());
        getCommand("statistics").setExecutor(new Info());
        getLogger().info("===[ Statistics enabled ]===");
    }

    public static Statistics getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("===[ Statistics disabled ]===");
    }
}
