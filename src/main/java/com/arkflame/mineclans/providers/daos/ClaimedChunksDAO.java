package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class ClaimedChunksDAO {
    private MySQLProvider mySQLProvider;

    public ClaimedChunksDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_claimed_chunks (" +
                "faction_id CHAR(36) NOT NULL," +
                "chunk_x INT NOT NULL," +
                "chunk_z INT NOT NULL," +
                "PRIMARY KEY (faction_id, chunk_x, chunk_z)," +
                "FOREIGN KEY (faction_id) REFERENCES mineclans_factions(faction_id) ON DELETE CASCADE)");
    }

    public void claimChunk(UUID factionId, int chunkX, int chunkZ) {
        String query = "INSERT INTO mineclans_claimed_chunks (faction_id, chunk_x, chunk_z) " +
                       "VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE faction_id = VALUES(faction_id)";
        mySQLProvider.executeUpdateQuery(query, factionId.toString(), chunkX, chunkZ);
    }

    public void unclaimChunk(UUID factionId, int chunkX, int chunkZ) {
        String query = "DELETE FROM mineclans_claimed_chunks WHERE faction_id = ? AND chunk_x = ? AND chunk_z = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString(), chunkX, chunkZ);
    }

    public void unclaimAllChunks(UUID factionId) {
        String query = "DELETE FROM mineclans_claimed_chunks WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }

    public ChunkCoordinate getChunkOwner(int chunkX, int chunkZ) {
        AtomicReference<ChunkCoordinate> result = new AtomicReference<>(null);
        String query = "SELECT faction_id, chunk_x, chunk_z FROM mineclans_claimed_chunks WHERE chunk_x = ? AND chunk_z = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    UUID factionId = UUID.fromString(resultSet.getString("faction_id"));
                    int x = resultSet.getInt("chunk_x");
                    int z = resultSet.getInt("chunk_z");
                    result.set(new ChunkCoordinate(factionId, x, z));
                }
            };
        }, chunkX, chunkZ);
        return result.get();
    }

    public Set<ChunkCoordinate> getClaimedChunks(UUID factionId) {
        AtomicReference<Set<ChunkCoordinate>> chunks = new AtomicReference<>(new HashSet<>());
        String query = "SELECT chunk_x, chunk_z FROM mineclans_claimed_chunks WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                Set<ChunkCoordinate> chunkSet = new HashSet<>();
                while (resultSet.next()) {
                    int x = resultSet.getInt("chunk_x");
                    int z = resultSet.getInt("chunk_z");
                    chunkSet.add(new ChunkCoordinate(factionId, x, z));
                }
                chunks.set(chunkSet);
            };
        }, factionId.toString());
        return chunks.get();
    }

    public int getClaimedChunkCount(UUID factionId) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        String query = "SELECT COUNT(*) AS count FROM mineclans_claimed_chunks WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    count.set(resultSet.getInt("count"));
                }
            };
        }, factionId.toString());
        return count.get();
    }
    
    public Map<UUID, Set<ChunkCoordinate>> getAllClaimedChunks() {
        AtomicReference<Map<UUID, Set<ChunkCoordinate>>> result = new AtomicReference<>(new ConcurrentHashMap<>());
        String query = "SELECT faction_id, chunk_x, chunk_z FROM mineclans_claimed_chunks";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                Map<UUID, Set<ChunkCoordinate>> chunksMap = new ConcurrentHashMap<>();
                while (resultSet.next()) {
                    UUID factionId = UUID.fromString(resultSet.getString("faction_id"));
                    int x = resultSet.getInt("chunk_x");
                    int z = resultSet.getInt("chunk_z");
                    
                    chunksMap.computeIfAbsent(factionId, k -> ConcurrentHashMap.newKeySet())
                            .add(new ChunkCoordinate(factionId, x, z));
                }
                result.set(chunksMap);
            };
        });
        return result.get();
    }
}