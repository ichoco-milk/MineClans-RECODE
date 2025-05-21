package com.arkflame.mineclans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.managers.FactionBenefitsManager;
import com.arkflame.mineclans.models.FactionPlayer;

public class FactionBenefitsListener implements Listener {
    private final FactionBenefitsManager benefitsManager;

    public FactionBenefitsListener() {
        this.benefitsManager = MineClans.getInstance().getFactionBenefitsManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Only check if player actually moved to a different chunk
        if (event.getFrom().getBlockX() >> 4 != event.getTo().getBlockX() >> 4 ||
                event.getFrom().getBlockZ() >> 4 != event.getTo().getBlockZ() >> 4) {
            MineClans.runAsync(() -> {
                // Player changed chunks, update their rank benefits status
                benefitsManager.updateRankBenefitsStatus(player);

                // Also update all other players since this player moving might affect
                // enemy detection in neighboring chunks
                benefitsManager.updateNearbyPlayersBenefits(player);
            });
            ;
        }
    }

    // Optional: Also update on teleport events
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Schedule update for next tick to ensure teleport is complete
        MineClans.runAsync(() -> {
            benefitsManager.updateRankBenefitsStatus(player);
            benefitsManager.updateNearbyPlayersBenefits(player);
        },
                1L);
    }

    // Update when players join/leave to recalculate enemy presence
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        // Schedule update for next tick
        MineClans.runAsync(() -> {
            benefitsManager.updateNearbyPlayersBenefits(event.getPlayer());
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        MineClans.runAsync(() -> {
            // Update all remaining players since an enemy might have left
            benefitsManager.updateNearbyPlayersBenefits(event.getPlayer());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player != null && player.isOnline()) {
                FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().get(player);
                if (factionPlayer != null && !factionPlayer.canReceiveDamage()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}