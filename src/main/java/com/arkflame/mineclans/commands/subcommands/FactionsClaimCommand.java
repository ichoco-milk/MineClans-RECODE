package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;

public class FactionsClaimCommand {
    private static final String BASE_PATH = "factions.claim.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();

        // Debug: Start of command execution
        mineClans.getLogger().info("[ClaimCommand] Claim command executed by " + player.getName());

        // Check if player is in a faction
        Faction faction = api.getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_in_faction")));
            mineClans.getLogger().info("[ClaimCommand] Player not in a faction");
            return;
        }

        // Check player permissions
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (!factionPlayer.getRank().isEqualOrHigherThan(Rank.COLEADER)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_permission")));
            mineClans.getLogger().info("[ClaimCommand] Player lacks permission to claim");
            return;
        }

        // Get current chunk
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Debug: Chunk coordinates
        mineClans.getLogger().info("[ClaimCommand] Attempting to claim chunk at X:" + chunkX + " Z:" + chunkZ);

        // Check if chunk is already claimed
        if (api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ)) {
            ChunkCoordinate existingClaim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ);
            String ownerName = api.getFaction(existingClaim.getFactionId()).getName();
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "already_claimed")
                    .replace("%faction%", ownerName)));
            mineClans.getLogger().info("[ClaimCommand] Chunk already claimed by " + ownerName);
            return;
        }

        // Check faction score/claim limit
        int currentClaims = api.getClaimedChunks().getClaimedChunkCount(faction.getId());
        int maxClaims = (int) faction.getScore() + 10; // Assuming score determines max claims

        if (currentClaims >= maxClaims) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "claim_limit_reached")
                    .replace("%current%", String.valueOf(currentClaims))
                    .replace("%max%", String.valueOf(maxClaims))));
            mineClans.getLogger().info("[ClaimCommand] Faction claim limit reached (" + currentClaims + "/" + maxClaims + ")");
            return;
        }

        // Attempt to claim the chunk
        boolean success = api.getClaimedChunks().claimChunk(faction.getId(), chunkX, chunkZ, true);
        
        if (success) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "success")
                    .replace("%x%", String.valueOf(chunkX))
                    .replace("%z%", String.valueOf(chunkZ))));
            mineClans.getLogger().info("[ClaimCommand] Successfully claimed chunk for faction " + faction.getName());
        } else {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "error")));
            mineClans.getLogger().warning("[ClaimCommand] Failed to claim chunk for unknown reason");
        }
    }
}