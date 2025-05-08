package com.arkflame.mineclans.providers.daos.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class InvitedDAO {
    protected String CREATE_INVITED_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_invited (" +
            "faction_id CHAR(36) PRIMARY KEY," +
            "member_id CHAR(36) NOT NULL)";

    protected String ADD_INVITED_MEMBER_QUERY = "INSERT INTO mineclans_invited (faction_id, member_id) " +
            "VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE faction_id = VALUES(faction_id), member_id = VALUES(member_id)";

    protected String REMOVE_INVITED_MEMBER_QUERY = "DELETE FROM mineclans_invited WHERE faction_id = ? AND member_id = ?";

    protected String IS_MEMBER_INVITED_QUERY = "SELECT 1 FROM mineclans_invited WHERE faction_id = ? AND member_id = ?";

    protected String GET_INVITED_MEMBERS_QUERY = "SELECT member_id FROM mineclans_invited WHERE faction_id = ?";

    protected String REMOVE_INVITED_MEMBERS_QUERY = "DELETE FROM mineclans_invited WHERE faction_id = ?";

    protected String GET_INVITING_FACTIONS_QUERY = "SELECT faction_id FROM mineclans_invited WHERE member_id = ?";

    private MySQLProvider mySQLProvider;

    public InvitedDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery(CREATE_INVITED_TABLE_QUERY);
    }

    public void addInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery(
                ADD_INVITED_MEMBER_QUERY,
                factionId, memberId);
    }

    public void removeInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery(REMOVE_INVITED_MEMBER_QUERY,
                factionId, memberId);
    }

    public boolean isMemberInvited(UUID factionId, UUID memberId) {
        AtomicBoolean isMemberInvited = new AtomicBoolean(false);
        mySQLProvider.executeSelectQuery(IS_MEMBER_INVITED_QUERY,
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        isMemberInvited.set(resultSet.next());
                    }
                }, factionId, memberId);
        return isMemberInvited.get();
    }

    public Collection<UUID> getInvitedMembers(UUID factionId) {
        Collection<UUID> invitedMembers = ConcurrentHashMap.newKeySet();
        mySQLProvider.executeSelectQuery(GET_INVITED_MEMBERS_QUERY, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        invitedMembers.add(UUID.fromString(resultSet.getString("member_id")));
                    }
                }
            }
        }, factionId);
        return invitedMembers;
    }

    public void removeInvitedMembers(UUID factionId) {
        String query = REMOVE_INVITED_MEMBERS_QUERY;
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }

    public Collection<UUID> getInvitingFactions(UUID memberId) {
        Collection<UUID> invitingFactions = ConcurrentHashMap.newKeySet();

        mySQLProvider.executeSelectQuery(GET_INVITING_FACTIONS_QUERY, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        invitingFactions.add(UUID.fromString(resultSet.getString("faction_id")));
                    }
                }
            }
        }, memberId);

        return invitingFactions;
    }
}
