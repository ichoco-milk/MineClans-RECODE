package com.arkflame.mineclans.providers.daos.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class FactionPlayerDAO {
    protected String CREATE_TABLES_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_players ("
            + "player_id TEXT NOT NULL PRIMARY KEY,"
            + "faction_id TEXT,"
            + "join_date TIMESTAMP,"
            + "last_active TIMESTAMP,"
            + "kills INT DEFAULT 0,"
            + "deaths INT DEFAULT 0,"
            + "name TEXT,"
            + "FOREIGN KEY (faction_id) REFERENCES mineclans_factions(faction_id) ON DELETE SET NULL"
            + ")";
    protected String INSERT_PLAYER_QUERY = "INSERT INTO mineclans_players (player_id, faction_id, join_date, last_active, kills, deaths, name) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "faction_id = VALUES(faction_id), join_date = VALUES(join_date), last_active = VALUES(last_active), "
            + "kills = VALUES(kills), deaths = VALUES(deaths), name = VALUES(name)";
    protected String SELECT_BY_ID_QUERY = "SELECT * FROM mineclans_players WHERE player_id = ?";
    protected String SELECT_BY_NAME_QUERY = "SELECT * FROM mineclans_players WHERE name = ?";
    protected String DELETE_PLAYER_QUERY = "DELETE FROM mineclans_players WHERE player_id = ?";
    private MySQLProvider mySQLProvider;

    public FactionPlayerDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery(CREATE_TABLES_QUERY);
    }

    public void insertOrUpdatePlayer(FactionPlayer player) {
        mySQLProvider.executeUpdateQuery(
                INSERT_PLAYER_QUERY,
                player.getPlayerId(),
                player.getFactionId(),
                player.getJoinDate(),
                player.getLastActive(),
                player.getKills(),
                player.getDeaths(),
                player.getName());
    }

    public FactionPlayer getPlayerById(UUID playerId) {
        AtomicReference<FactionPlayer> player = new AtomicReference<>(null);
        mySQLProvider.executeSelectQuery(SELECT_BY_ID_QUERY,
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet != null && resultSet.next()) {
                            player.set(extractPlayerFromResultSet(resultSet));
                        }
                    }
                }, playerId.toString());
        return player.get();
    }

    public FactionPlayer getPlayerByName(String name) {
        AtomicReference<FactionPlayer> player = new AtomicReference<>(null);
        mySQLProvider.executeSelectQuery(SELECT_BY_NAME_QUERY,
                new ResultSetProcessor() {
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet != null && resultSet.next()) {
                            player.set(extractPlayerFromResultSet(resultSet));
                        }
                    };
                },
                name);
        return player.get();
    }

    private FactionPlayer extractPlayerFromResultSet(ResultSet resultSet) throws SQLException {
        FactionPlayer player = new FactionPlayer(UUID.fromString(resultSet.getString("player_id")));
        player.setFactionId(resultSet.getString("faction_id"));
        player.setJoinDate(resultSet.getTimestamp("join_date"));
        player.setLastActive(resultSet.getTimestamp("last_active"));
        player.setKills(resultSet.getInt("kills"));
        player.setDeaths(resultSet.getInt("deaths"));
        player.setName(resultSet.getString("name"));
        return player;
    }

    public void deletePlayer(UUID playerId) {
        mySQLProvider.executeUpdateQuery(DELETE_PLAYER_QUERY, playerId);
    }
}
