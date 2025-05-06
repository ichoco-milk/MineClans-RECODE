package com.arkflame.mineclans.claims;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.providers.daos.ClaimedChunksDAO;

public class ClaimedChunks {
    private final Map<UUID, Set<ChunkCoordinate>> claimedChunksByFaction = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, ChunkCoordinate>> claimedChunksMap = new ConcurrentHashMap<>();
    private final Set<ChunkCoordinate> claimedChunks = ConcurrentHashMap.newKeySet();
    
    private final ClaimedChunksDAO claimedChunksDAO;

    public ClaimedChunks(ClaimedChunksDAO claimedChunksDAO) {
        this.claimedChunksDAO = claimedChunksDAO;
        loadAllClaimedChunks();
    }

    /**
     * Loads all claimed chunks from the database into memory
     */
    public void loadAllClaimedChunks() {
        // Clear existing data
        claimedChunksByFaction.clear();
        claimedChunksMap.clear();
        claimedChunks.clear();
        
        // Load all chunks from the database
        Map<UUID, Set<ChunkCoordinate>> allChunks = claimedChunksDAO.getAllClaimedChunks();
        
        // Populate the data structures
        for (Map.Entry<UUID, Set<ChunkCoordinate>> entry : allChunks.entrySet()) {
            claimedChunksByFaction.put(entry.getKey(), entry.getValue());
            for (ChunkCoordinate chunk : entry.getValue()) {
                addChunkToMaps(chunk);
            }
        }
    }

    /**
     * Loads claimed chunks for a specific faction
     * @param factionId The faction ID to load chunks for
     */
    public void loadClaimedChunks(UUID factionId) {
        Set<ChunkCoordinate> chunks = claimedChunksDAO.getClaimedChunks(factionId);
        claimedChunksByFaction.put(factionId, chunks);
        for (ChunkCoordinate chunk : chunks) {
            addChunkToMaps(chunk);
        }
    }

    private void addChunkToMaps(ChunkCoordinate chunk) {
        claimedChunks.add(chunk);
        claimedChunksMap.computeIfAbsent(chunk.getX(), k -> new ConcurrentHashMap<>())
                        .put(chunk.getZ(), chunk);
    }

    private void removeChunkFromMaps(ChunkCoordinate chunk) {
        claimedChunks.remove(chunk);
        Map<Integer, ChunkCoordinate> zMap = claimedChunksMap.get(chunk.getX());
        if (zMap != null) {
            zMap.remove(chunk.getZ());
            if (zMap.isEmpty()) {
                claimedChunksMap.remove(chunk.getX());
            }
        }
    }

    public boolean claimChunk(UUID factionId, int x, int z, boolean publishUpdate) {
        if (isChunkClaimed(x, z)) {
            return false;
        }
        
        ChunkCoordinate chunk = new ChunkCoordinate(factionId, x, z);
        claimedChunksDAO.claimChunk(factionId, x, z);
        
        claimedChunksByFaction.computeIfAbsent(factionId, k -> ConcurrentHashMap.newKeySet())
                             .add(chunk);
        addChunkToMaps(chunk);

        if (publishUpdate) {
            MineClans.getInstance().getMySQLProvider().getClaimedChunksDAO().claimChunk(factionId, x, z);
            MineClans.getInstance().getRedisProvider().updateChunk(factionId, x, z, false);
        }
        
        return true;
    }

    public boolean unclaimChunk(UUID factionId, int x, int z, boolean publishUpdate) {
        ChunkCoordinate chunk = getChunkAt(x, z);
        if (chunk == null || !chunk.getFactionId().equals(factionId)) {
            return false;
        }
        
        claimedChunksDAO.unclaimChunk(factionId, x, z);
        
        Set<ChunkCoordinate> factionChunks = claimedChunksByFaction.get(factionId);
        if (factionChunks != null) {
            factionChunks.remove(chunk);
            if (factionChunks.isEmpty()) {
                claimedChunksByFaction.remove(factionId);
            }
        }
        removeChunkFromMaps(chunk);

        if (publishUpdate) {
            MineClans.getInstance().getMySQLProvider().getClaimedChunksDAO().unclaimChunk(factionId, x, z);
            MineClans.getInstance().getRedisProvider().updateChunk(factionId, x, z, true);
        }
        
        return true;
    }

    public void unclaimAllChunks(UUID factionId) {
        Set<ChunkCoordinate> chunks = claimedChunksByFaction.remove(factionId);
        if (chunks != null) {
            unclaimChunk(factionId, 0, 0, true);
        }
    }

    public boolean isChunkClaimed(int x, int z) {
        Map<Integer, ChunkCoordinate> zMap = claimedChunksMap.get(x);
        boolean exist = zMap != null && zMap.containsKey(z);
        if (zMap == null || !zMap.containsKey(z)) {
            return false;
        }
        UUID factionId = exist ? zMap.get(z).getFactionId() : null;
        if (factionId == null) {
            return false;
        }
        if (MineClans.getInstance().getFactionManager().getFaction(factionId) == null) {
            return false;
        }
        return true;
    }

    public ChunkCoordinate getChunkAt(int x, int z) {
        if (!isChunkClaimed(x, z)) {
            return null;
        }
        Map<Integer, ChunkCoordinate> zMap = claimedChunksMap.get(x);
        return zMap != null ? zMap.get(z) : null;
    }

    public Set<ChunkCoordinate> getClaimedChunks(UUID factionId) {
        return claimedChunksByFaction.getOrDefault(factionId, ConcurrentHashMap.newKeySet());
    }

    public int getClaimedChunkCount(UUID factionId) {
        return claimedChunksByFaction.getOrDefault(factionId, ConcurrentHashMap.newKeySet()).size();
    }

    public Set<ChunkCoordinate> getAllClaimedChunks() {
        return claimedChunks;
    }
}