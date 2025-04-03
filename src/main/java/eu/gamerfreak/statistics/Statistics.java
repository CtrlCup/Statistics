package eu.gamerfreak.statistics;

import eu.gamerfreak.statistics.commands.Info;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



import java.io.File;

public final class Statistics extends JavaPlugin {

    private FileConfiguration messages;
    private static Statistics instance;
    private Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getCommand("statistics").setExecutor(new Info());

        getLogger().info("\u001B[36m===[ \u001B[0m\u001B[32mStatistics \u001B[0m\u001B[36m]===\u001B[0m");
        getLogger().info("");
        getLogger().info("\u001B[35mPlugin version: \u001B[32m" + getDescription().getVersion() + "\u001B[0m");
        getLogger().info("\u001B[35mMinecraft version: \u001B[32m" + getServer().getBukkitVersion() + "\u001B[0m");

        saveDefaultConfig();
        saveResource("permissions.yml", true);
        saveResource("messages.yml", false);
        loadMessages();

        // MySQL Part
        host = getConfig().getString("MySQL.Host");
        database = getConfig().getString("MySQL.Database");
        username = getConfig().getString("MySQL.User");
        password = getConfig().getString("MySQL.Password");
        port = getConfig().getInt("MySQL.Port");

        connectToDatabase();    // Verbindung zur MySQL herstellen

        createTable();  // Tabelle erstellen (falls noch nicht vorhanden)

        if (checkConnection()) {
            getLogger().info("\u001B[35mMySQL connection: \u001B[32mSuccessful\u001B[0m");
        }
        else {
            getLogger().info("\u001B[35mMySQL connection: \u001B[32mFailed\u001B[0m");
        }


        getLogger().info("");
        getLogger().info("\u001B[35mby \u001B[33mGamerfreak_LP\u001B[0m");
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


    private void connectToDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
                        username, password);
            }
        } catch (SQLException e) {
            getLogger().severe("Konnte keine Verbindung zur MySQL-Datenbank herstellen: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            getLogger().severe("MySQL JDBC Treiber nicht gefunden: " + e.getMessage());
        }
    }

    private void createTable() {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS spieler_daten ("
                            + "uuid VARCHAR(36) PRIMARY KEY,"
                            + "name VARCHAR(16) NOT NULL,"
                            + "punkte INT DEFAULT 0,"
                            + "letzte_anmeldung TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                            + ")");
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Fehler beim Erstellen der Tabelle: " + e.getMessage());
        }
    }

    // Beispielmethode zum Speichern von Spielerdaten
    public void savePlayerData(String uuid, String name, int points) {
        try {
            // Sicherstellen, dass die Verbindung aktiv ist
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spieler_daten (uuid, name, punkte) VALUES (?, ?, ?) "
                            + "ON DUPLICATE KEY UPDATE name = ?, punkte = ?, letzte_anmeldung = NOW()");
            ps.setString(1, uuid);
            ps.setString(2, name);
            ps.setInt(3, points);
            ps.setString(4, name);
            ps.setInt(5, points);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Fehler beim Speichern der Spielerdaten: " + e.getMessage());
        }
    }

    // Beispielmethode zum Laden von Spielerdaten
    public int getPlayerPoints(String uuid) {
        try {
            // Sicherstellen, dass die Verbindung aktiv ist
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT punkte FROM spieler_daten WHERE uuid = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("punkte");
            }
        } catch (SQLException e) {
            getLogger().severe("Fehler beim Laden der Spielerdaten: " + e.getMessage());
        }

        return 0; // Standardwert, falls keine Daten gefunden wurden
    }

    // Methode, um die Verbindung zu prüfen und ggf. wiederherzustellen
    public boolean checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
                return connection != null && !connection.isClosed();
            }
            return true;
        } catch (SQLException e) {
            getLogger().severe("Fehler bei der Verbindungsprüfung: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logicif (connection != null)
        if (connection != null) {
            try {
                connection.close();
                getLogger().info("MySQL-Verbindung geschlossen!");
            } catch (SQLException e) {
                getLogger().severe("Fehler beim Schließen der MySQL-Verbindung: " + e.getMessage());
            }
        }
        getLogger().info("===[ Statistics disabled ]===");
    }
}
