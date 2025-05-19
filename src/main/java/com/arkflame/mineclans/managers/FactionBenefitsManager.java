package com.arkflame.mineclans.managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.utils.Players;

public class FactionBenefitsManager {
    private final MineClansAPI api;

    public FactionBenefitsManager() {
        this.api = MineClans.getInstance().getAPI();
    }

    /**
     * Checks if a player can use rank benefits (fly/god mode) based on:
     * 1. Player has the benefit enabled
     * 2. Current chunk is from the same faction as the player
     * 3. Current and neighboring chunks don't have enemy players
     */
    public boolean canUseRankBenefits(Player player) {
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (factionPlayer == null) {
            return false;
        }

        // Check if player has any benefits enabled
        if (!factionPlayer.isFlying() && !factionPlayer.isGodMode()) {
            return true; // No benefits to check
        }

        // Check if player is in a faction
        Faction playerFaction = factionPlayer.getFaction();
        if (playerFaction == null) {
            return false;
        }

        Location playerLoc = player.getLocation();
        int centerX = playerLoc.getBlockX() >> 4;
        int centerZ = playerLoc.getBlockZ() >> 4;
        String worldName = playerLoc.getWorld().getName();

        // Check if current chunk is claimed by same faction
        if (!isChunkClaimedBySameFaction(centerX, centerZ, worldName, playerFaction.getId())) {
            return false;
        }

        // Check all 9 chunks (3x3 grid around player) for enemy players
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                int chunkX = centerX + xOffset;
                int chunkZ = centerZ + zOffset;

                if (hasEnemyPlayersInChunk(chunkX, chunkZ, worldName, playerFaction.getId())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if a chunk is claimed by the same faction as the player
     */
    private boolean isChunkClaimedBySameFaction(int chunkX, int chunkZ, String worldName, UUID factionId) {
        if (!api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ, worldName)) {
            return false;
        }

        ChunkCoordinate claim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ, worldName);
        return claim != null && factionId.equals(claim.getFactionId());
    }

    /**
     * Checks if there are enemy players in or near a specific chunk
     * 
     * @param centerChunkX    The center chunk X coordinate
     * @param centerChunkZ    The center chunk Z coordinate
     * @param worldName       The world name to check in
     * @param playerFactionId The UUID of the faction to check against
     * @param radius          How many chunks around the center to check (0 = just
     *                        the center chunk)
     * @return true if enemy players are found in the specified area
     */
    private boolean hasEnemyPlayersNearChunk(int centerChunkX, int centerChunkZ, String worldName,
            UUID playerFactionId, int radius) {
        // Check all chunks in the radius around the center chunk
        for (int x = centerChunkX - radius; x <= centerChunkX + radius; x++) {
            for (int z = centerChunkZ - radius; z <= centerChunkZ + radius; z++) {
                Set<UUID> playerIds = playersInChunks.getOrDefault(worldName, Collections.emptyMap())
                        .getOrDefault(x, Collections.emptyMap())
                        .getOrDefault(z, Collections.emptySet());

                for (UUID playerId : playerIds) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player == null || !player.isOnline()) {
                        continue;
                    }

                    FactionPlayer otherFactionPlayer = api.getFactionPlayer(player);
                    if (otherFactionPlayer == null || otherFactionPlayer.getFactionId() == null) {
                        // No faction = enemy
                        return true;
                    }

                    if (!otherFactionPlayer.getFactionId().equals(playerFactionId)) {
                        // Different faction = enemy
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Helper method with default radius of 1 (checks 3x3 area)
    private boolean hasEnemyPlayersInChunk(int chunkX, int chunkZ, String worldName, UUID playerFactionId) {
        return hasEnemyPlayersNearChunk(chunkX, chunkZ, worldName, playerFactionId, 1);
    }

    /**
     * Updates the rank benefits status for a player based on current conditions
     */
    public void updateRankBenefitsStatus(Player player) {
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (factionPlayer == null) {
            return;
        }
        boolean canUseBenefits = canUseRankBenefits(player);
        // Handle flying
        if (factionPlayer.isFlying()) {
            if (canUseBenefits) {
                // Enable flying if not already enabled
                if (!player.getAllowFlight()) {
                    Players.setFlying(player, true);
                }
            } else {
                // Disable flying
                Players.setFlying(player, false);
            }
        }

        // Handle god mode
        if (factionPlayer.isGodMode()) {
            if (canUseBenefits) {
                // God mode is enabled in benefit object, actual implementation
                // would depend on how god mode is handled in your plugin
                // This is just the status tracking
            } else {
                // God mode should be disabled due to conditions
                // Actual implementation would depend on your god mode system
            }
        }
    }

    private Map<String, Map<Integer, Map<Integer, Set<UUID>>>> playersInChunks = new ConcurrentHashMap<>();
    private Map<UUID, int[]> playerChunkMap = new ConcurrentHashMap<>();

    public void addPlayerToChunk(Player player) {
        if (player == null) {
            return;
        }

        UUID playerId = player.getUniqueId();
        Location loc = player.getLocation();
        int chunkX = loc.getBlockX() >> 4;
        int chunkZ = loc.getBlockZ() >> 4;
        String worldName = loc.getWorld().getName();

        playerChunkMap.put(playerId, new int[] { chunkX, chunkZ });
        playersInChunks.computeIfAbsent(worldName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(chunkX, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(chunkZ, k -> ConcurrentHashMap.newKeySet())
                .add(playerId);
    }

    public void removePlayerFromChunk(Player player) {
        if (player == null) {
            return;
        }

        UUID playerId = player.getUniqueId();
        int[] oldChunk = playerChunkMap.remove(playerId);
        if (oldChunk == null) {
            return;
        }

        String worldName = player.getLocation().getWorld().getName();
        removePlayerFromSpecificChunk(worldName, oldChunk[0], oldChunk[1], playerId);
    }

    private void removePlayerFromSpecificChunk(String worldName, int chunkX, int chunkZ, UUID playerId) {
        Map<Integer, Map<Integer, Set<UUID>>> worldMap = playersInChunks.get(worldName);
        if (worldMap == null)
            return;

        Map<Integer, Set<UUID>> xMap = worldMap.get(chunkX);
        if (xMap == null)
            return;

        Set<UUID> chunkSet = xMap.get(chunkZ);
        if (chunkSet == null)
            return;

        chunkSet.remove(playerId);

        // Clean up empty structures
        if (chunkSet.isEmpty()) {
            xMap.remove(chunkZ);
            if (xMap.isEmpty()) {
                worldMap.remove(chunkX);
                if (worldMap.isEmpty()) {
                    playersInChunks.remove(worldName);
                }
            }
        }
    }

    public Set<UUID> getPlayersInChunk(int chunkX, int chunkZ, String worldName) {
        return playersInChunks.getOrDefault(worldName, Collections.emptyMap())
                .getOrDefault(chunkX, Collections.emptyMap())
                .getOrDefault(chunkZ, Collections.emptySet());
    }

    public Set<UUID> getPlayersInChunk(Player player) {
        if (player == null) {
            return Collections.emptySet();
        }
        Location loc = player.getLocation();
        return getPlayersInChunk(
                loc.getBlockX() >> 4,
                loc.getBlockZ() >> 4,
                loc.getWorld().getName());
    }

    public void updateChunks(Player player) {
        if (player == null) {
            return;
        }

        UUID playerId = player.getUniqueId();
        Location loc = player.getLocation();
        String worldName = loc.getWorld().getName();
        int newChunkX = loc.getBlockX() >> 4;
        int newChunkZ = loc.getBlockZ() >> 4;

        // Get old chunk and check if we need to update
        int[] oldChunk = playerChunkMap.get(playerId);
        if (oldChunk != null && oldChunk[0] == newChunkX && oldChunk[1] == newChunkZ) {
            return; // Player hasn't changed chunks
        }

        // Remove from old chunk if exists
        if (oldChunk != null) {
            removePlayerFromSpecificChunk(worldName, oldChunk[0], oldChunk[1], playerId);
        }

        // Add to new chunk
        playerChunkMap.put(playerId, new int[] { newChunkX, newChunkZ });
        playersInChunks.computeIfAbsent(worldName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(newChunkX, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(newChunkZ, k -> ConcurrentHashMap.newKeySet())
                .add(playerId);
    }

    /**
     * Updates benefits for all players near the specified player
     * 
     * @param player The center player whose nearby players should be updated
     */
    public void updateNearbyPlayersBenefits(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        // Get player's faction info
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (factionPlayer == null) {
            return;
        }

        // Get player's current chunk location
        Location loc = player.getLocation();
        int centerChunkX = loc.getBlockX() >> 4;
        int centerChunkZ = loc.getBlockZ() >> 4;
        String worldName = loc.getWorld().getName();

        // Define radius (in chunks) for "nearby" players
        final int BENEFIT_RADIUS = 2; // 2 chunks = ~32 blocks radius

        // Track which players we've already processed to avoid duplicates
        Set<UUID> processedPlayers = new HashSet<>();

        // Include the center player first
        processedPlayers.add(player.getUniqueId());
        updateRankBenefitsStatus(player);

        // Check all chunks in the radius around the player
        for (int x = centerChunkX - BENEFIT_RADIUS; x <= centerChunkX + BENEFIT_RADIUS; x++) {
            for (int z = centerChunkZ - BENEFIT_RADIUS; z <= centerChunkZ + BENEFIT_RADIUS; z++) {
                // Get all players in this chunk
                Set<UUID> playerIds = playersInChunks.getOrDefault(worldName, Collections.emptyMap())
                        .getOrDefault(x, Collections.emptyMap())
                        .getOrDefault(z, Collections.emptySet());

                // Process each player in the chunk
                for (UUID playerId : playerIds) {
                    // Skip if we've already processed this player
                    if (processedPlayers.contains(playerId)) {
                        continue;
                    }

                    Player nearbyPlayer = Bukkit.getPlayer(playerId);
                    if (nearbyPlayer != null && nearbyPlayer.isOnline()) {
                        updateRankBenefitsStatus(nearbyPlayer);
                        processedPlayers.add(playerId);
                    }
                }
            }
        }
    }
}