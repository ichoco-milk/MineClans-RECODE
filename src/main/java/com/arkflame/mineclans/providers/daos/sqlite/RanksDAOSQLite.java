package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.RanksDAO;

public class RanksDAOSQLite extends RanksDAO {
    public RanksDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_ranks ("
                + "player_id TEXT PRIMARY KEY,"
                + "player_rank TEXT NOT NULL)";
        this.INSERT_OR_UPDATE_RANK_QUERY = "INSERT OR REPLACE INTO mineclans_ranks (player_id, player_rank) VALUES (?, ?)";
    }
}