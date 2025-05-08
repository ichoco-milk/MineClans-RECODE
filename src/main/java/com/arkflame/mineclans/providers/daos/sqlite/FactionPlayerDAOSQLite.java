package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.FactionPlayerDAO;

public class FactionPlayerDAOSQLite extends FactionPlayerDAO {
    public FactionPlayerDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_TABLES_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_players ("
                + "player_id TEXT PRIMARY KEY,"
                + "faction_id TEXT,"
                + "join_date TIMESTAMP,"
                + "last_active TIMESTAMP,"
                + "kills INTEGER DEFAULT 0,"
                + "deaths INTEGER DEFAULT 0,"
                + "name TEXT)";
        this.INSERT_PLAYER_QUERY = "INSERT OR REPLACE INTO mineclans_players (player_id, faction_id, join_date, last_active, kills, deaths, name) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
}