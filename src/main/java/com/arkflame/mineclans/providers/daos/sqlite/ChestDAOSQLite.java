package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.ChestDAO;

public class ChestDAOSQLite extends ChestDAO {
    public ChestDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_chests ("
                + "faction_id TEXT NOT NULL PRIMARY KEY, "
                + "chest_contents TEXT"
                + ")";
        this.INSERT_CHEST_QUERY = "INSERT OR REPLACE INTO mineclans_chests (faction_id, chest_contents) VALUES (?, ?)";
        this.SELECT_BY_FACTION_ID_QUERY = "SELECT chest_contents FROM mineclans_chests WHERE faction_id = ?";
        this.DELECT_CHEST_QUERY = "DELETE FROM mineclans_chests WHERE faction_id = ?";
    }
}