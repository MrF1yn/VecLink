package dev.mrflyn.veclink.databases;


import com.google.gson.Gson;
import dev.mrflyn.veclink.Main;
import dev.mrflyn.veclinkcommon.IDatabase;
import dev.mrflyn.veclinkcommon.VLPlayer;


import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class SQLite implements IDatabase {
    Gson gson = new Gson();
    private String url;

    private Connection connection;

    @Override
    public String name() {
        return "SQLite";
    }

    @Override
    public boolean connect() {
        File folder = new File(Main.gi.getConfigLocation() + "/database");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Main.gi.log("Could not create /Cache folder!");
            }
        }
        File dataFolder = new File(folder.getPath() + "/cache.db");
        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) {
                    Main.gi.log("Could not create /Cache/cache.db file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        this.url = "jdbc:sqlite:" + dataFolder;
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            if (e instanceof ClassNotFoundException) {
                Main.gi.log("Could Not Found SQLite Driver on your system!");
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void init() {
        try  {
            String sql = "CREATE TABLE IF NOT EXISTS discord_data (id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id VARCHAR(200), user_name VARCHAR(200), minecraft_uuid VARCHAR(200), minecraft_name VARCHAR(200));";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void saveUser(String userID, String userName, UUID minecraftUUID, String minecraftName) {
        String sql = "INSERT INTO discord_data (user_id, user_name, minecraft_uuid, minecraft_name) VALUES (?, ?, ?, ?);";
        try {
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, userID);
                statement.setString(2, userName);
                statement.setString(3, minecraftUUID.toString());
                statement.setString(4, minecraftName);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public VLPlayer getPlayerInfoFromUserID(String userID) {
        String sql = "SELECT * FROM discord_data WHERE user_id=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userID);
                ResultSet result = statement.executeQuery();
                if(result.next()){
                    return new VLPlayer(
                            UUID.fromString(result.getString("minecraft_uuid")),
                            result.getString("minecraft_name"),
                            userID,
                            result.getString("user_name")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VLPlayer getPlayerInfoFromUserName(String userName) {
        String sql = "SELECT * FROM discord_data WHERE user_name=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userName);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new VLPlayer(
                            UUID.fromString(result.getString("minecraft_uuid")),
                            result.getString("minecraft_name"),
                            result.getString("user_id"),
                            userName
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VLPlayer getPlayerInfoFromMinecraftName(String name) {
        String sql = "SELECT * FROM discord_data WHERE minecraft_name=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new VLPlayer(
                            UUID.fromString(result.getString("minecraft_uuid")),
                            name,
                            result.getString("user_id"),
                            result.getString("user_name")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VLPlayer getPlayerInfoFromMinecraftUUID(UUID uuid) {
        String sql = "SELECT * FROM discord_data WHERE minecraft_uuid=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new VLPlayer(
                            uuid,
                            result.getString("minecraft_name"),
                            result.getString("user_id"),
                            result.getString("user_name")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void deletePlayerInfoFromUserID(String userID) {
        String sql = "DELETE FROM discord_data WHERE user_id=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deletePlayerInfoFromMinecraftUUID(UUID uuid) {
        String sql = "DELETE FROM discord_data WHERE minecraft_uuid=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
