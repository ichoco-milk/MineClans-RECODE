package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.ClaimedChunksDAO;

public class ClaimedChunksDAOSQLite extends ClaimedChunksDAO {
    public ClaimedChunksDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_TABLES_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_chunks ("
                + "faction_id TEXT NOT NULL,"
                + "chunk_x INTEGER NOT NULL,"
                + "chunk_z INTEGER NOT NULL,"
                + "server_name TEXT NOT NULL,"
                + "world_name TEXT NOT NULL,"
                + "claim_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (faction_id, chunk_x, chunk_z, server_name, world_name))";
        this.CLAIM_CHUNK_QUERY = "INSERT OR REPLACE INTO mineclans_chunks (faction_id, chunk_x, chunk_z, server_name, world_name, claim_date) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    }
}