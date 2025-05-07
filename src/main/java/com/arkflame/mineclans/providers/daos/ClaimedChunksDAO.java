package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
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
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_chunks (" +
                "faction_id CHAR(36) NOT NULL," +
                "chunk_x INT NOT NULL," +
                "chunk_z INT NOT NULL," +
                "server_name VARCHAR(64) NOT NULL," +
                "world_name VARCHAR(64) NOT NULL," +
                "claim_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (faction_id, chunk_x, chunk_z, server_name, world_name)," +
                "FOREIGN KEY (faction_id) REFERENCES mineclans_factions(faction_id) ON DELETE CASCADE)");
    }

    public void claimChunk(UUID factionId, int chunkX, int chunkZ, String worldName, String serverName) {
        String query = "INSERT INTO mineclans_chunks (faction_id, chunk_x, chunk_z, server_name, world_name, claim_date) " +
                       "VALUES (?, ?, ?, ?, ?, NOW()) " +
                       "ON DUPLICATE KEY UPDATE faction_id = VALUES(faction_id), claim_date = NOW()";
        mySQLProvider.executeUpdateQuery(query, factionId.toString(), chunkX, chunkZ, worldName, serverName);
    }

    public void unclaimChunk(int chunkX, int chunkZ, String worldName, String serverName) {
        String query = "DELETE FROM mineclans_chunks WHERE chunk_x = ? AND chunk_z = ? AND server_name = ? AND world_name = ?";
        mySQLProvider.executeUpdateQuery(query, chunkX, chunkZ, worldName, serverName);
    }

    public void unclaimAllChunks(UUID factionId) {
        String query = "DELETE FROM mineclans_chunks WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }

    public ChunkCoordinate getChunkOwner(int chunkX, int chunkZ, String worldName, String serverName) {
        AtomicReference<ChunkCoordinate> result = new AtomicReference<>(null);
        String query = "SELECT faction_id, chunk_x, chunk_z, server_name, world_name, claim_date FROM mineclans_chunks " +
                       "WHERE chunk_x = ? AND chunk_z = ? AND server_name = ? AND world_name = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    UUID factionId = UUID.fromString(resultSet.getString("faction_id"));
                    int x = resultSet.getInt("chunk_x");
                    int z = resultSet.getInt("chunk_z");
                    String server = resultSet.getString("server_name");
                    String world = resultSet.getString("world_name");
                    Date claimDate = resultSet.getTimestamp("claim_date");
                    result.set(new ChunkCoordinate(factionId, x, z, server, world, claimDate));
                }
            };
        }, chunkX, chunkZ, worldName, serverName);
        return result.get();
    }

    public Set<ChunkCoordinate> getClaimedChunks(UUID factionId) {
        AtomicReference<Set<ChunkCoordinate>> chunks = new AtomicReference<>(new HashSet<>());
        String query = "SELECT chunk_x, chunk_z, server_name, world_name, claim_date FROM mineclans_chunks WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                Set<ChunkCoordinate> chunkSet = new HashSet<>();
                while (resultSet.next()) {
                    int x = resultSet.getInt("chunk_x");
                    int z = resultSet.getInt("chunk_z");
                    String server = resultSet.getString("server_name");
                    String world = resultSet.getString("world_name");
                    Date claimDate = resultSet.getTimestamp("claim_date");
                    chunkSet.add(new ChunkCoordinate(factionId, x, z, server, world, claimDate));
                }
                chunks.set(chunkSet);
            };
        }, factionId.toString());
        return chunks.get();
    }

    public int getClaimedChunkCount(UUID factionId) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        String query = "SELECT COUNT(*) AS count FROM mineclans_chunks WHERE faction_id = ?";
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
        String query = "SELECT faction_id, chunk_x, chunk_z, server_name, world_name, claim_date FROM mineclans_chunks";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                Map<UUID, Set<ChunkCoordinate>> chunksMap = new ConcurrentHashMap<>();
                while (resultSet.next()) {
                    UUID factionId = UUID.fromString(resultSet.getString("faction_id"));
                    int x = resultSet.getInt("chunk_x");
                    int z = resultSet.getInt("chunk_z");
                    String server = resultSet.getString("server_name");
                    String world = resultSet.getString("world_name");
                    Date claimDate = resultSet.getTimestamp("claim_date");
                    
                    chunksMap.computeIfAbsent(factionId, k -> ConcurrentHashMap.newKeySet())
                            .add(new ChunkCoordinate(factionId, x, z, server, world, claimDate));
                }
                result.set(chunksMap);
            };
        });
        return result.get();
    }
}