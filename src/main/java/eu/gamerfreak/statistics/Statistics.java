package eu.gamerfreak.statistics;

import eu.gamerfreak.statistics.commands.Info;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Statistics extends JavaPlugin {

    private FileConfiguration messages;
    private static Statistics instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        saveResource("permissions.yml", true);
        saveResource("messages.yml", false);
        loadMessages();


        getCommand("statistics").setExecutor(new Info());
        getLogger().info("\u001B[36m===[ \u001B[0m\u001B[32mStatistics \u001B[0m\u001B[36m]===\u001B[0m");
        getLogger().info("");
        getLogger().info("\u001B[32mPlugin version: \u001B[35m" + Statistics.getInstance().getDescription().getVersion() + "\u001B[0m");
        getLogger().info("\u001B[32mMinecraft version: \u001B[35m" + Statistics.getInstance().getServer().getBukkitVersion() + "\u001B[0m");
        getLogger().info("");
        getLogger().info("\u001B[32mby \u001B[33mGamerfreak_LP\u001B[0m");
        getLogger().info("");
        getLogger().info("\u001B[36m===[ \u001B[0m\u001B[32mStatistics \u001B[0m\u001B[36m]===\u001B[0m");
    }

    public static Statistics getInstance() {
        return instance;
    }

    public void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String msg = messages.getString(path);
        return msg != null ? ChatColor.translateAlternateColorCodes('&', msg) : "§cNachricht fehlt: " + path;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("===[ Statistics disabled ]===");
    }
}
