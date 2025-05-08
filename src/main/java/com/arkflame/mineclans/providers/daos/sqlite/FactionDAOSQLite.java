package com.arkflame.mineclans.providers.daos.sqlite;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.daos.mysql.FactionDAO;

public class FactionDAOSQLite extends FactionDAO {
    public FactionDAOSQLite(MySQLProvider mySQLProvider) {
        super(mySQLProvider);
        this.CREATE_FACTIONS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_factions ("
                + "faction_id TEXT PRIMARY KEY,"
                + "owner_id TEXT NOT NULL,"
                + "display_name TEXT NOT NULL,"
                + "home TEXT,"
                + "name TEXT UNIQUE,"
                + "balance REAL,"
                + "kills INTEGER,"
                + "events_won INTEGER,"
                + "friendly_fire BOOLEAN,"
                + "open BOOLEAN,"
                + "creation_date TIMESTAMP,"
                + "announcement TEXT,"
                + "discord TEXT)";
        this.UPSERT_FACTION_QUERY = "INSERT OR REPLACE INTO mineclans_factions ("
                + "faction_id, owner_id, display_name, home, name, balance, kills, events_won, friendly_fire, open, creation_date, announcement, discord) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
}
