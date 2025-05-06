package com.arkflame.mineclans.providers.daos;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ScoreDAO {
    private final MySQLProvider mySQLProvider;

    public ScoreDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_score ("
                + "faction_id VARCHAR(36) NOT NULL, "
                + "score DOUBLE, "
                + "PRIMARY KEY (faction_id), "
                + "INDEX idx_score (score))");
    }

    public Double getFactionScore(UUID factionId) {
        final Double[] score = {null};
        mySQLProvider.executeSelectQuery("SELECT score FROM mineclans_score WHERE faction_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            score[0] = resultSet.getDouble("score");
                        }
                    }
                }, factionId.toString());
        return score[0];
    }

    public void updateFactionScore(UUID factionId, double score) {
        mySQLProvider.executeUpdateQuery(
                "INSERT INTO mineclans_score (faction_id, score) VALUES (?, ?) "
                        + "ON DUPLICATE KEY UPDATE score = VALUES(score)",
                factionId.toString(), score);
    }

    public int getFactionPosition(UUID factionId) {
        final int[] position = {0};
        mySQLProvider.executeSelectQuery(
                "SELECT (SELECT COUNT(*) FROM mineclans_score AS mp WHERE mp.score > m.score " +
                        "OR (mp.score = m.score AND mp.faction_id < m.faction_id)) AS idx_score " +
                        "FROM mineclans_score AS m WHERE m.faction_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            position[0] = resultSet.getInt("idx_score");
                        }
                    }
                }, factionId.toString());
        return position[0] + 1; // Adjusting for 1-based index
    }    

    public UUID getFactionIdByPosition(int position) {
        final UUID[] factionId = {null};
        mySQLProvider.executeSelectQuery(
                "SELECT faction_id FROM (SELECT faction_id, RANK() OVER (ORDER BY score DESC, faction_id ASC) as rank FROM mineclans_score) ranked WHERE rank = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            factionId[0] = UUID.fromString(resultSet.getString("faction_id"));
                        }
                    }
                }, position);
        return factionId[0];
    }

    public void removeFaction(UUID factionId) {
        mySQLProvider.executeUpdateQuery(
                "DELETE FROM mineclans_score WHERE faction_id = ?",
                factionId.toString());
    }
}
