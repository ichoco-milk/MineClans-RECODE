package com.arkflame.mineclans.claims;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.models.ChunkCoordinate;

/**
 * Manages a mapping between faction IDs and their claimed chunks
 */
public class FactionChunkMap {
    private final Map<UUID, Set<ChunkCoordinate>> chunksByFaction = new ConcurrentHashMap<>();
    
    /**
     * Clear all data from the map
     */
    public void clear() {
        chunksByFaction.clear();
    }
    
    /**
     * Add a chunk to a faction's claim set
     * 
     * @param factionId The faction ID
     * @param chunk The chunk to add
     */
    public void addChunk(UUID factionId, ChunkCoordinate chunk) {
        chunksByFaction.computeIfAbsent(factionId, k -> ConcurrentHashMap.newKeySet())
                .add(chunk);
    }
    
    /**
     * Add multiple chunks to a faction's claim set
     * 
     * @param factionId The faction ID
     * @param chunks The set of chunks to add
     */
    public void addChunks(UUID factionId, Set<ChunkCoordinate> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return;
        }
        
        chunksByFaction.computeIfAbsent(factionId, k -> ConcurrentHashMap.newKeySet())
                .addAll(chunks);
    }
    
    /**
     * Remove a chunk from a faction's claim set
     * 
     * @param factionId The faction ID
     * @param chunk The chunk to remove
     * @return true if the chunk was removed, false otherwise
     */
    public boolean removeChunk(UUID factionId, ChunkCoordinate chunk) {
        Set<ChunkCoordinate> factionChunks = chunksByFaction.get(factionId);
        if (factionChunks == null) {
            return false;
        }
        
        boolean removed = factionChunks.remove(chunk);
        
        // Clean up empty sets
        if (factionChunks.isEmpty()) {
            chunksByFaction.remove(factionId);
        }
        
        return removed;
    }
    
    /**
     * Get all chunks claimed by a faction
     * 
     * @param factionId The faction ID
     * @return A set of claimed chunks
     */
    public Set<ChunkCoordinate> getChunks(UUID factionId) {
        return chunksByFaction.getOrDefault(factionId, ConcurrentHashMap.newKeySet());
    }
    
    /**
     * Get the number of chunks claimed by a faction
     * 
     * @param factionId The faction ID
     * @return The number of claimed chunks
     */
    public int getChunkCount(UUID factionId) {
        return getChunks(factionId).size();
    }
    
    /**
     * Check if a faction has any claimed chunks
     * 
     * @param factionId The faction ID
     * @return true if the faction has claimed chunks, false otherwise
     */
    public boolean hasClaims(UUID factionId) {
        Set<ChunkCoordinate> chunks = chunksByFaction.get(factionId);
        return chunks != null && !chunks.isEmpty();
    }
    
    /**
     * Get all factions with claims
     * 
     * @return A set of faction IDs
     */
    public Set<UUID> getFactionsWithClaims() {
        return chunksByFaction.keySet();
    }
}