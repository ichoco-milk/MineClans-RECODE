package com.arkflame.mineclans.claims;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.models.ChunkCoordinate;

/**
 * Manages a mapping between chunk coordinates and chunk data across servers and worlds
 */
public class WorldChunkMap {
    // server -> world -> x -> z -> chunk
    private final Map<String, Map<String, Map<Integer, Map<Integer, ChunkCoordinate>>>> chunkData = new ConcurrentHashMap<>();
    
    // Keep a flat set of all chunks for easy access
    private final Set<ChunkCoordinate> allChunks = ConcurrentHashMap.newKeySet();
    
    /**
     * Clear all data from the map
     */
    public void clear() {
        chunkData.clear();
        allChunks.clear();
    }
    
    /**
     * Add a chunk to the map
     * 
     * @param chunk The chunk to add
     */
    public void addChunk(ChunkCoordinate chunk) {
        String serverName = chunk.getServerName();
        String worldName = chunk.getWorldName();
        int x = chunk.getX();
        int z = chunk.getZ();
        
        chunkData.computeIfAbsent(serverName, s -> new ConcurrentHashMap<>())
                .computeIfAbsent(worldName, w -> new ConcurrentHashMap<>())
                .computeIfAbsent(x, xCoord -> new ConcurrentHashMap<>())
                .put(z, chunk);
        
        allChunks.add(chunk);
    }
    
    /**
     * Remove a chunk from the map
     * 
     * @param chunk The chunk to remove
     * @return true if the chunk was removed, false otherwise
     */
    public boolean removeChunk(ChunkCoordinate chunk) {
        String serverName = chunk.getServerName();
        String worldName = chunk.getWorldName();
        int x = chunk.getX();
        int z = chunk.getZ();
        
        // Get the maps
        Map<String, Map<Integer, Map<Integer, ChunkCoordinate>>> worldMap = chunkData.get(serverName);
        if (worldMap == null) return false;
        
        Map<Integer, Map<Integer, ChunkCoordinate>> xMap = worldMap.get(worldName);
        if (xMap == null) return false;
        
        Map<Integer, ChunkCoordinate> zMap = xMap.get(x);
        if (zMap == null) return false;
        
        // Remove the chunk
        ChunkCoordinate removed = zMap.remove(z);
        boolean success = removed != null;
        
        // Clean up empty maps
        if (zMap.isEmpty()) {
            xMap.remove(x);
            if (xMap.isEmpty()) {
                worldMap.remove(worldName);
                if (worldMap.isEmpty()) {
                    chunkData.remove(serverName);
                }
            }
        }
        
        // Remove from flat set
        allChunks.remove(chunk);
        
        return success;
    }
    
    /**
     * Get a chunk from the map
     * 
     * @param x The chunk x coordinate
     * @param z The chunk z coordinate
     * @param serverName The server name
     * @param worldName The world name
     * @return The chunk, or null if not found
     */
    public ChunkCoordinate getChunk(int x, int z, String worldName, String serverName) {
        Map<String, Map<Integer, Map<Integer, ChunkCoordinate>>> worldMap = chunkData.get(serverName);
        if (worldMap == null) return null;
        
        Map<Integer, Map<Integer, ChunkCoordinate>> xMap = worldMap.get(worldName);
        if (xMap == null) return null;
        
        Map<Integer, ChunkCoordinate> zMap = xMap.get(x);
        if (zMap == null) return null;
        
        return zMap.get(z);
    }
    
    /**
     * Get all chunks for a specific world
     * 
     * @param serverName The server name
     * @param worldName The world name
     * @return A set of chunks in the world
     */
    public Set<ChunkCoordinate> getChunksInWorld(String worldName, String serverName) {
        Set<ChunkCoordinate> result = ConcurrentHashMap.newKeySet();
        
        Map<String, Map<Integer, Map<Integer, ChunkCoordinate>>> worldMap = chunkData.get(serverName);
        if (worldMap == null) return result;
        
        Map<Integer, Map<Integer, ChunkCoordinate>> xMap = worldMap.get(worldName);
        if (xMap == null) return result;
        
        // Collect all chunks from the z maps
        for (Map<Integer, ChunkCoordinate> zMap : xMap.values()) {
            result.addAll(zMap.values());
        }
        
        return result;
    }
    
    /**
     * Get all chunks across all servers and worlds
     * 
     * @return A set of all chunks
     */
    public Set<ChunkCoordinate> getAllChunks() {
        return allChunks;
    }
    
    /**
     * Get the total number of claimed chunks
     * 
     * @return The number of chunks
     */
    public int getTotalChunkCount() {
        return allChunks.size();
    }
}