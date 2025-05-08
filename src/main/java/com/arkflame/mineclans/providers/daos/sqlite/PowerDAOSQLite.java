package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.PowerDAO;

public class PowerDAOSQLite extends PowerDAO {
    public PowerDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_POWER_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_player_power ("
                + "player_id TEXT PRIMARY KEY,"
                + "power REAL DEFAULT 10.0)";
        this.UPSERT_POWER_QUERY = "INSERT OR REPLACE INTO mineclans_player_power (player_id, power) VALUES (?, ?)";
    }
}