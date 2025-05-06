package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

/**
 * Data Access Object for managing player power in the database.
 */
public class PowerDAO {
    private MySQLProvider mySQLProvider;
    
    // Default power values based on classic Factions
    private static final double DEFAULT_POWER = 10.0;
    private static final double MAX_POWER = 10.0;
    private static final double MIN_POWER = -10.0;
    private static final double POWER_PER_DEATH = 5.0;
    private static final double POWER_PER_HOUR = 2.0;
    
    // Cache for power values to reduce database queries
    private Map<UUID, Double> powerCache = new HashMap<>();

    public PowerDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    /**
     * Creates the power table if it doesn't exist.
     */
    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_player_power ("
                + "player_id CHAR(36) NOT NULL PRIMARY KEY,"
                + "power DOUBLE NOT NULL DEFAULT " + DEFAULT_POWER + ","
                + "FOREIGN KEY (player_id) REFERENCES mineclans_players(player_id) ON DELETE CASCADE"
                + ")");
    }

    /**
     * Gets the power of a player. Returns default power if not found.
     * 
     * @param playerId The UUID of the player
     * @return The player's power
     */
    public double getPower(UUID playerId) {
        // Check cache first
        if (powerCache.containsKey(playerId)) {
            return powerCache.get(playerId);
        }
        
        AtomicReference<Double> power = new AtomicReference<>(DEFAULT_POWER);
        
        mySQLProvider.executeSelectQuery("SELECT power FROM mineclans_player_power WHERE player_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet != null && resultSet.next()) {
                            power.set(resultSet.getDouble("power"));
                        }
                    }
                }, playerId.toString());
        
        // Update cache
        powerCache.put(playerId, power.get());
        return power.get();
    }

    /**
     * Sets the power for a player.
     * 
     * @param playerId The UUID of the player
     * @param power The power value to set
     */
    public void setPower(UUID playerId, double power) {
        // Ensure power is within limits
        double clampedPower = Math.max(MIN_POWER, Math.min(MAX_POWER, power));
        
        mySQLProvider.executeUpdateQuery(
                "INSERT INTO mineclans_player_power (player_id, power) "
                        + "VALUES (?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "power = ?",
                playerId.toString(),
                clampedPower,
                clampedPower);
        
        // Update cache
        powerCache.put(playerId, clampedPower);
    }
    
    /**
     * Clears the power cache for a specific player.
     * 
     * @param playerId The UUID of the player
     */
    public void clearCache(UUID playerId) {
        powerCache.remove(playerId);
    }
    
    /**
     * Clears the entire power cache.
     */
    public void clearCache() {
        powerCache.clear();
    }
    
    /**
     * Gets the maximum power a player can have.
     * 
     * @return The maximum power value
     */
    public double getMaxPower() {
        return MAX_POWER;
    }
    
    /**
     * Gets the minimum power a player can have.
     * 
     * @return The minimum power value
     */
    public double getMinPower() {
        return MIN_POWER;
    }
    
    /**
     * Gets the amount of power lost per death.
     * 
     * @return The power lost per death
     */
    public double getPowerPerDeath() {
        return POWER_PER_DEATH;
    }
    
    /**
     * Gets the amount of power gained per hour played.
     * 
     * @return The power gained per hour
     */
    public double getPowerPerHour() {
        return POWER_PER_HOUR;
    }
}