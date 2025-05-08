package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.ScoreDAO;

public class ScoreDAOSQLite extends ScoreDAO {
    public ScoreDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_score ("
                + "faction_id TEXT PRIMARY KEY,"
                + "score REAL)";
        this.INSERT_OR_UPDATE_SCORE_QUERY = "INSERT OR REPLACE INTO mineclans_score (faction_id, score) VALUES (?, ?)";
    }
}