package com.arkflame.mineclans.providers.daos.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class MemberDAO {
    protected String CREATE_MEMBERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS mineclans_members (" +
            "faction_id CHAR(36) NOT NULL," +
            "member_id CHAR(36) NOT NULL," +
            "PRIMARY KEY (faction_id, member_id))";

    protected String ADD_MEMBER_QUERY = "INSERT INTO mineclans_members (faction_id, member_id) VALUES (?, ?) "
            +
            "ON DUPLICATE KEY UPDATE member_id = VALUES(member_id)";

    protected String REMOVE_MEMBER_QUERY = "DELETE FROM mineclans_members WHERE faction_id = ? AND member_id = ?";

    protected String REMOVE_MEMBERS_BY_FACTION_QUERY = "DELETE FROM mineclans_members WHERE faction_id = ?";

    protected String SELECT_MEMBERS_BY_FACTION_QUERY = "SELECT member_id FROM mineclans_members WHERE faction_id = ?";

    protected String GET_FACTION_BY_MEMBER_QUERY = "SELECT faction_id FROM mineclans_members WHERE member_id = ?";

    private MySQLProvider mySQLProvider;

    public MemberDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery(CREATE_MEMBERS_TABLE_QUERY);
    }

    public void addMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery(ADD_MEMBER_QUERY, factionId.toString(), memberId.toString());
    }

    public void removeMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery(
                REMOVE_MEMBER_QUERY,
                factionId.toString(),
                memberId.toString());
    }

    public void removeMembers(UUID factionId) {
        mySQLProvider.executeUpdateQuery(REMOVE_MEMBERS_BY_FACTION_QUERY, factionId.toString());
    }

    public Collection<UUID> getMembers(UUID factionId) {
        Collection<UUID> members = ConcurrentHashMap.newKeySet();
        mySQLProvider.executeSelectQuery(SELECT_MEMBERS_BY_FACTION_QUERY, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        UUID memberId = UUID.fromString(resultSet.getString("member_id"));
                        members.add(memberId);
                    }
                }
            }
        }, factionId.toString());
        return members;
    }
}
