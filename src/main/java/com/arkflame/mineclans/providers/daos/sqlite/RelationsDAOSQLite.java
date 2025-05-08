package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.RelationsDAO;

public class RelationsDAOSQLite extends RelationsDAO {
    public RelationsDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_relations ("
                + "faction_id TEXT NOT NULL,"
                + "target_faction_id TEXT NOT NULL,"
                + "relation_type TEXT NOT NULL,"
                + "PRIMARY KEY (faction_id, target_faction_id))";
        this.INSERT_OR_UPDATE_RELATION_QUERY = "INSERT OR REPLACE INTO mineclans_relations (faction_id, target_faction_id, relation_type) VALUES (?, ?, ?)";
    }
}