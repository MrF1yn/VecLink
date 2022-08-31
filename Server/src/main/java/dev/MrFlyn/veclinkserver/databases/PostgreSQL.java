package dev.mrflyn.veclinkserver.databases;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mrflyn.veclinkcommon.IDatabase;
import dev.mrflyn.veclinkcommon.VLPlayer;
import dev.mrflyn.veclinkserver.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;




public class PostgreSQL implements IDatabase {
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


    public PostgreSQL() {
        this.host = Main.config.getDbConfig().getString("storage.postgresql.host");
        this.database = Main.config.getDbConfig().getString("storage.postgresql.database");
        this.user = Main.config.getDbConfig().getString("storage.postgresql.username");
        this.pass = Main.config.getDbConfig().getString("storage.postgresql.password");
        this.port = Main.config.getDbConfig().getInt("storage.postgresql.port");
        this.ssl = Main.config.getDbConfig().getBoolean("storage.postgresql.ssl");
        this.certificateVerification = Main.config.getDbConfig().getBoolean("storage.postgresql.verify-certificate", true);
        this.poolSize = Main.config.getDbConfig().getInt("storage.postgresql.pool-size", 10);
        this.maxLifetime = Main.config.getDbConfig().getInt("storage.postgresql.max-lifetime", 1800);
    }

    @Override
    public String name() {
        return "PostgreSQL";
    }

    /**
     * Creates the SQL connection pool and tries to connect.
     *
     * @return true if connected successfully.
     */
    public boolean connect() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("VecLinkServerPostgreSQLPool");
//        hikariConfig.addDataSourceProperty("dataSourceClassName", "dev.mrflyn.replayaddon.libs.org.postgresql.Driver");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);

        hikariConfig.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);

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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS discord_data (id SERIAL PRIMARY KEY, " +
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

