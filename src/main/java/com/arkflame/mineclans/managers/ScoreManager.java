package com.arkflame.mineclans.managers;

import com.arkflame.mineclans.providers.daos.mysql.ScoreDAO;

import java.util.UUID;

public class ScoreManager {
    private final ScoreDAO scoreDAO;
    private final LeaderboardManager leaderboardManager;

    public ScoreManager(ScoreDAO scoreDAO, LeaderboardManager leaderboardManager) {
        this.scoreDAO = scoreDAO;
        this.leaderboardManager = leaderboardManager;
    }

    public void updateScore(UUID factionId, double newScore) {
        // Update the score in the database
        scoreDAO.updateFactionScore(factionId, newScore);
        // Notify the leaderboard manager about the score update
        leaderboardManager.onFactionUpdateScore(factionId);
    }
}
