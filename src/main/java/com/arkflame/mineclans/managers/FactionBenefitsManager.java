package com.arkflame.mineclans.managers;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.commands.subcommands.FactionsFlyCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsGodCommand;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.modernlib.utils.Players;

public class FactionBenefitsManager {
    private final MineClansAPI api;

    private Map<String, Set<UUID>> playersInChunks = new ConcurrentHashMap<>();
    private Map<UUID, String> playerChunks = new ConcurrentHashMap<>();
    private ConfigWrapper messages;

    public FactionBenefitsManager() {
        this.api = MineClans.getInstance().getAPI();
        this.messages = MineClans.getInstance().getMessages();
    }

    public String getChunkKey(String worldName, int chunkX, int chunkZ) {
        return worldName + ":" + chunkX + ":" + chunkZ;
    }

    public Collection<UUID> getPlayersInChunk(String worldName, int chunkX, int chunkZ) {
        String key = getChunkKey(worldName, chunkX, chunkZ);
        return playersInChunks.getOrDefault(key, ConcurrentHashMap.newKeySet());
    }

    public Collection<UUID> getNearbyPlayers(String worldName, int chunkX, int chunkZ, int radius) {
        Collection<UUID> nearbyPlayers = ConcurrentHashMap.newKeySet();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                String key = getChunkKey(worldName, chunkX + x, chunkZ + z);
                if (playersInChunks.containsKey(key)) {
                    nearbyPlayers.addAll(playersInChunks.get(key));
                }
            }
        }
        return nearbyPlayers;
    }

    public Collection<UUID> getNearbyPlayers(Player player, int radius) {
        Location location = player.getLocation();
        return getNearbyPlayers(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4,
                radius);
    }

    public void setChunk(String worldName, int chunkX, int chunkZ, UUID playerId) {
        String key = getChunkKey(worldName, chunkX, chunkZ);
        // Remove from old chunks
        if (playerChunks.containsKey(playerId)) {
            playersInChunks.get(playerChunks.get(playerId)).remove(playerId);
        }
        // Add to new chunks
        playersInChunks.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(playerId);
        playerChunks.put(playerId, key);
    }

    private boolean isChunkClaimedBySameFaction(int chunkX, int chunkZ, String worldName, UUID factionId) {
        if (!api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ, worldName)) {
            return false;
        }

        ChunkCoordinate claim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ, worldName);
        return claim != null && factionId.equals(claim.getFactionId());
    }

    public boolean canUseRankBenefits(FactionPlayer factionPlayer, Player player) {
        // Check if player is in a faction
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            return false;
        }

        Location location = player.getLocation();
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        String worldName = location.getWorld().getName();

        // Check if current chunk is claimed by same faction
        if (!isChunkClaimedBySameFaction(chunkX, chunkZ, worldName, faction.getId())) {
            return false;
        }

        Collection<UUID> playersInChunk = getNearbyPlayers(player, 1);
        if (playersInChunk.isEmpty()) {
            return true;
        }
        UUID playerId = player.getUniqueId();
        for (UUID playerInChunkId : playersInChunk) {
            if (playerInChunkId != null && playerInChunkId != playerId) {
                if (!faction.isMember(playerInChunkId)) {
                    System.out.println(playerId);
                    System.out.println(playerInChunkId);
                    return false;
                }
            }
        }

        return true;
    }

    public void updateBenefits(Player player) {
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (factionPlayer == null) {
            return;
        }
        if (factionPlayer.isGodMode() || factionPlayer.isFlying()) {
            boolean canUseRankBenefits = canUseRankBenefits(factionPlayer, player);
            if (factionPlayer.isGodMode()) {
                boolean invulnerable = !canUseRankBenefits;
                if (factionPlayer.canReceiveDamage() != invulnerable) {
                    factionPlayer.setCanReceiveDamage(invulnerable);
                    if (invulnerable) {
                        player.sendMessage(
                                ChatColors.color(messages.getText(FactionsGodCommand.BASE_PATH + "activated")));
                    } else {
                        player.sendMessage(
                                ChatColors.color(messages.getText(FactionsGodCommand.BASE_PATH + "deactivated")));
                    }
                }
            }
            if (factionPlayer.isFlying()) {
                boolean canFly = canUseRankBenefits;
                if (player.getAllowFlight() != canFly) {
                    Players.setFlying(player, canFly);
                    if (canFly) {
                        player.sendMessage(
                                ChatColors.color(messages.getText(FactionsFlyCommand.BASE_PATH + "activated")));
                    } else {
                        player.sendMessage(
                                ChatColors.color(messages.getText(FactionsFlyCommand.BASE_PATH + "deactivated")));
                    }
                }
            }
        }
    }

    public void updateBenefits(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            updateBenefits(player);
        }
    }

    public void setChunk(Player player, Location to) {
        int chunkX = to.getBlockX() >> 4;
        int chunkZ = to.getBlockZ() >> 4;
        String worldName = to.getWorld().getName();
        setChunk(worldName, chunkX, chunkZ, player.getUniqueId());
    }
}