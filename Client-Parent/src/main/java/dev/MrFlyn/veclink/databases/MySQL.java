package dev.mrflyn.veclink.databases;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mrflyn.veclinkcommon.IDatabase;
import dev.mrflyn.veclinkcommon.VLPlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



public class MySQL implements IDatabase {
    Gson gson = new Gson();

    private HikariDataSource dataSource;
    private final String host;
    private final String database;
    private final String user;
    private final String pass;
    private final int port;
    private final boolean ssl;
    private final boolean certificateVerification;
    private final int poolSize;
    private final int maxLifetime;

    public MySQL(String host, String database, String user, String pass, int port, boolean ssl, boolean certificateVerification, int poolSize, int maxLifetime) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.pass = pass;
        this.port = port;
        this.ssl = ssl;
        this.certificateVerification = certificateVerification;
        this.poolSize = poolSize;
        this.maxLifetime = maxLifetime;
    }

    @Override
    public String name() {
        return "MySQL";
    }

    /**
     * Creates the SQL connection pool and tries to connect.
     *
     * @return true if connected successfully.
     */
    @Override
    public boolean connect() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName("VecLinkClientMySQLPool");

        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);

        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Recover if connection gets interrupted
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    public boolean isConnected(){
        return dataSource.isRunning();
    }

    @Override
    public void disconnect() {
        dataSource.close();
    }

    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS discord_data (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
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
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

