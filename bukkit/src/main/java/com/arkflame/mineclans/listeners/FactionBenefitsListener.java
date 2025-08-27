package com.arkflame.mineclans.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.managers.FactionBenefitsManager;
import com.arkflame.mineclans.tasks.FactionBenefitsTask;

public class FactionBenefitsListener implements Listener {
    private final FactionBenefitsTask benefitsTask;
    private final FactionBenefitsManager benefitsManager;

    public FactionBenefitsListener() {
        this.benefitsTask = MineClans.getInstance().getFactionBenefitsTask();
        this.benefitsManager = MineClans.getInstance().getFactionBenefitsManager();
    }

    public void updateNearby(Player player) {
        MineClans.runAsync(() -> {
            // Get nearby players
            for (UUID playerId : benefitsManager.getNearbyPlayers(player, 1)) {
                // Schedule for benefit update
                benefitsTask.scheduleUpdate(playerId);
            }
        });
    }

    public void updateNearby(String worldName, int chunkX, int chunkZ) {
        MineClans.runAsync(() -> {
            // Get nearby players
            for (UUID playerId : benefitsManager.getNearbyPlayers(worldName, chunkX, chunkZ, 1)) {
                // Schedule for benefit update
                benefitsTask.scheduleUpdate(playerId);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getPlayer().isOnline()) {
            return;
        }
        int fromChunkX = event.getFrom().getBlockX() >> 4;
        int fromChunkZ = event.getFrom().getBlockZ() >> 4;
        int toChunkX = event.getTo().getBlockX() >> 4;
        int toChunkZ = event.getTo().getBlockZ() >> 4;

        // Skip if chunk didnt change
        if (fromChunkX == toChunkX && fromChunkZ == toChunkZ) {
            return;
        }

        // Update chunk
        benefitsManager.setChunk(event.getPlayer(), event.getTo());
        updateNearby(event.getFrom().getWorld().getName(), fromChunkX, fromChunkZ);
        updateNearby(event.getTo().getWorld().getName(), toChunkX, toChunkZ);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Update chunk
        updateNearby(event.getFrom().getWorld().getName(),
                event.getFrom().getBlockX() >> 4, event.getFrom().getBlockZ() >> 4);
        benefitsManager.setChunk(event.getPlayer(), event.getTo());
        updateNearby(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update chunk
        benefitsManager.setChunk(event.getPlayer(), event.getPlayer().getLocation());
        updateNearby(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Update chunk
        benefitsManager.setChunk(event.getPlayer(), null);
        updateNearby(event.getPlayer());
    }
}