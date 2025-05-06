package com.arkflame.mineclans.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.tasks.TeleportScheduler;

public class PlayerMoveListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        MineClans.runAsync(() -> {
            Player player = event.getPlayer();
            TeleportScheduler teleportScheduler = MineClans.getInstance().getTeleportScheduler();

            // Check if player is teleporting
            if (teleportScheduler.isTeleporting(player)) {
                // Ignore if only the player's head rotated (yaw/pitch change)
                if (event.getFrom().getX() == event.getTo().getX() &&
                        event.getFrom().getY() == event.getTo().getY() &&
                        event.getFrom().getZ() == event.getTo().getZ()) {
                    return; // No actual movement, only head rotation
                }

                // Get the original teleport start location
                Location startLocation = teleportScheduler.getStartLocation(player);
                if (startLocation != null) {
                    // Check if the player moved more than 1 block from the starting location
                    if (startLocation.distance(event.getTo()) > 1.0) {
                        ConfigWrapper messages = MineClans.getInstance().getMessages();
                        String basePath = "factions.home.";
                        teleportScheduler.cancelTeleport(player);
                        player.sendMessage(messages.getText(basePath + "cancelled"));
                    }
                }
            }

            // Player changes chunks
            if (event.getFrom().getChunk() != event.getTo().getChunk()) {
                // Get plugin instance once to avoid repeated calls
                MineClans plugin = MineClans.getInstance();

                // Get chunk coordinates
                int fromX = event.getFrom().getChunk().getX();
                int fromZ = event.getFrom().getChunk().getZ();
                int toX = event.getTo().getChunk().getX();
                int toZ = event.getTo().getChunk().getZ();

                // Get chunk claim data
                ChunkCoordinate fromChunk = plugin.getClaimedChunks().getChunkAt(fromX, fromZ);
                ChunkCoordinate toChunk = plugin.getClaimedChunks().getChunkAt(toX, toZ);

                // Get faction IDs (may be null if chunk is not claimed)
                UUID fromFactionId = fromChunk != null ? fromChunk.getFactionId() : null;
                UUID toFactionId = toChunk != null ? toChunk.getFactionId() : null;

                // Handle leaving message - only if we're actually leaving a faction's territory
                // to wilderness
                // or to a different faction's territory
                if (fromChunk != null
                        && (toChunk == null || (fromFactionId == null || !fromFactionId.equals(toFactionId)))) {
                    Faction fromFaction = plugin.getFactionManager().getFaction(fromFactionId);
                    String fromFactionName = fromFaction != null ? fromFaction.getName() : "Unknown";

                    player.sendMessage(plugin.getMessages().getText("factions.claims.left.message",
                            "%x%", String.valueOf(fromX),
                            "%z%", String.valueOf(fromZ),
                            "%owner%", fromFactionName));
                }

                // Handle entering message - only if we're entering a faction's territory from
                // wilderness
                // or from a different faction's territory
                if (toChunk != null
                        && (fromChunk == null || (toFactionId != null && !toFactionId.equals(fromFactionId)))) {
                    Faction toFaction = plugin.getFactionManager().getFaction(toFactionId);
                    String toFactionName = toFaction != null ? toFaction.getName() : "Unknown";

                    player.sendMessage(plugin.getMessages().getText("factions.claims.entered.message",
                            "%x%", String.valueOf(toX),
                            "%z%", String.valueOf(toZ),
                            "%owner%", toFactionName));
                }
            }
        });
    }
}
