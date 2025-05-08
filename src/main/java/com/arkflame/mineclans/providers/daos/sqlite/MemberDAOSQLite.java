package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.MemberDAO;

public class MemberDAOSQLite extends MemberDAO {
    public MemberDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_MEMBERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_members ("
                + "faction_id TEXT NOT NULL,"
                + "member_id TEXT NOT NULL,"
                + "PRIMARY KEY (faction_id, member_id))";
        this.ADD_MEMBER_QUERY = "INSERT OR REPLACE INTO mineclans_members (faction_id, member_id) VALUES (?, ?)";
    }
}