package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.InvitedDAO;

public class InvitedDAOSQLite extends InvitedDAO {
    public InvitedDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_INVITED_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_invited ("
                + "faction_id TEXT NOT NULL,"
                + "member_id TEXT NOT NULL,"
                + "PRIMARY KEY (faction_id, member_id))";
        this.ADD_INVITED_MEMBER_QUERY = "INSERT OR REPLACE INTO mineclans_invited (faction_id, member_id) VALUES (?, ?)";
    }
}