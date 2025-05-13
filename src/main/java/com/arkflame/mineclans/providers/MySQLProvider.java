package com.arkflame.mineclans.providers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.providers.daos.mysql.ChestDAO;
import com.arkflame.mineclans.providers.daos.mysql.ClaimedChunksDAO;
import com.arkflame.mineclans.providers.daos.mysql.FactionDAO;
import com.arkflame.mineclans.providers.daos.mysql.FactionPlayerDAO;
import com.arkflame.mineclans.providers.daos.mysql.InvitedDAO;
import com.arkflame.mineclans.providers.daos.mysql.MemberDAO;
import com.arkflame.mineclans.providers.daos.mysql.PowerDAO;
import com.arkflame.mineclans.providers.daos.mysql.RanksDAO;
import com.arkflame.mineclans.providers.daos.mysql.RelationsDAO;
import com.arkflame.mineclans.providers.daos.mysql.ScoreDAO;
import com.arkflame.mineclans.providers.daos.sqlite.ChestDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.ClaimedChunksDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.FactionDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.FactionPlayerDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.InvitedDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.MemberDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.PowerDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.RanksDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.RelationsDAOSQLite;
import com.arkflame.mineclans.providers.daos.sqlite.ScoreDAOSQLite;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLProvider {
    private HikariConfig config;
    private HikariDataSource dataSource = null;

    private ChestDAO chestDAO;
    private FactionDAO factionDAO;
    private FactionPlayerDAO factionPlayerDAO;
    private InvitedDAO invitedDAO;
    private MemberDAO memberDAO;
    private RanksDAO ranksDAO;
    private RelationsDAO relationsDAO;
    private ScoreDAO scoreDAO;
    private ClaimedChunksDAO claimedChunksDAO;
    private PowerDAO powerDAO;

    private boolean connected = false;
    private boolean sqlite = false;

    public MySQLProvider(boolean enabled, String url, String username, String password) {
        Logger logger = MineClans.getInstance().getLogger();
        try {
            if (!enabled || url == null || username == null || password == null) {
                MineClans.getInstance().getLogger().severe("No database information provided.");
                try {
                    String sqliteFile = new File(MineClans.getInstance().getDataFolder(), "mineclans.db")
                            .getAbsolutePath();
                    url = "jdbc:sqlite:" + sqliteFile;
                    sqlite = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                MineClans.getInstance().getLogger().info("Using MySQL database for factions.");
            }

            if (sqlite) {
                chestDAO = new ChestDAOSQLite(this);
                factionDAO = new FactionDAOSQLite(this);
                factionPlayerDAO = new FactionPlayerDAOSQLite(this);
                invitedDAO = new InvitedDAOSQLite(this);
                memberDAO = new MemberDAOSQLite(this);
                ranksDAO = new RanksDAOSQLite(this);
                relationsDAO = new RelationsDAOSQLite(this);
                scoreDAO = new ScoreDAOSQLite(this);
                claimedChunksDAO = new ClaimedChunksDAOSQLite(this);
                powerDAO = new PowerDAOSQLite(this);
            } else {
                chestDAO = new ChestDAO(this);
                factionDAO = new FactionDAO(this);
                factionPlayerDAO = new FactionPlayerDAO(this);
                invitedDAO = new InvitedDAO(this);
                memberDAO = new MemberDAO(this);
                ranksDAO = new RanksDAO(this);
                relationsDAO = new RelationsDAO(this);
                scoreDAO = new ScoreDAO(this);
                claimedChunksDAO = new ClaimedChunksDAO(this);
                powerDAO = new PowerDAO(this);
            }

            // Generate hikari config
            generateHikariConfig(url, username, password);

            // Initialize
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("An error occurred while connecting to the database.");
        } finally {
            if (!isConnected()) {
                logger.severe("=============== DATABASE CONNECTION ERROR ================");
                logger.severe("MineClans is unable to connect to the database.");
                logger.severe("To fix this, please configure the database settings in the 'config.yml' file.");
                logger.severe("You need a MySQL database for the plugin to work properly.");
                logger.severe("You can also set MySQL to false in the 'config.yml' file to use SQLite.");
                logger.severe("=============== DATABASE CONNECTION ERROR ================");
                Bukkit.getPluginManager().disablePlugin(MineClans.getInstance());
                return;
            }
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public void generateHikariConfig(String url, String username, String password) {
        config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTestQuery("SELECT 1"); // Example query for connection testing
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    public ChestDAO getChestDAO() {
        return chestDAO;
    }

    public FactionDAO getFactionDAO() {
        return factionDAO;
    }

    public MemberDAO getMemberDAO() {
        return memberDAO;
    }

    public InvitedDAO getInvitedDAO() {
        return invitedDAO;
    }

    public RelationsDAO getRelationsDAO() {
        return relationsDAO;
    }

    public ScoreDAO getScoreDAO() {
        return scoreDAO;
    }

    public RanksDAO getRanksDAO() {
        return ranksDAO;
    }

    public FactionPlayerDAO getFactionPlayerDAO() {
        return factionPlayerDAO;
    }

    public boolean isConnected() {
        return connected;
    }

    public void createTables() {
        chestDAO.createTable();
        memberDAO.createTable();
        factionDAO.createTable();
        invitedDAO.createTable();
        relationsDAO.createTable();
        ranksDAO.createTable();
        factionPlayerDAO.createTable();
        scoreDAO.createTable();
        claimedChunksDAO.createTable();
        powerDAO.createTable();
    }

    public void initialize() {
        try {
            this.dataSource = new HikariDataSource(config);
            createTables();
            this.connected = true;
        } catch (Exception e) {
            MineClans.getInstance().getLogger().info("Failed to initialize database connection: " + e.getMessage());
            this.dataSource = null; // Ensure dataSource is null to avoid any further usage attempts
        }
    }

    public void executeUpdateQuery(String query, Object... params) {
        if (Bukkit.isPrimaryThread()) {
            MineClans.getInstance().getLogger().severe("WARNING: This method should not be called from the main thread.");
            new Exception().printStackTrace();
        }
        if (dataSource == null) {
            return;
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i] instanceof UUID ? params[i].toString() : params[i]);
            }
            // Execute query
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeSelectQuery(String query, ResultSetProcessor task, Object... params) {
        if (Bukkit.isPrimaryThread()) {
            MineClans.getInstance().getLogger().severe("WARNING: This method should not be called from the main thread.");
            new Exception().printStackTrace();
        }
        if (dataSource == null) {
            return;
        }
        try {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query);) {
                // Set parameters
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i] instanceof UUID ? params[i].toString() : params[i]);
                }
                // Execute query and return result set
                try (ResultSet result = statement.executeQuery()) {
                    task.run(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ClaimedChunksDAO getClaimedChunksDAO() {
        return claimedChunksDAO;
    }

    public PowerDAO getPowerDAO() {
        return powerDAO;
    }
}
