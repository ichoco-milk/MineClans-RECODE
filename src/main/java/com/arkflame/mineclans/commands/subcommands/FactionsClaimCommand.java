package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.api.results.ClaimResult;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsClaimCommand {
    private static final String BASE_PATH = "factions.claim.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();

        // Check if player is in a faction
        Faction faction = api.getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_in_faction")));
            return;
        }

        // Check player permissions
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (!factionPlayer.getRank().isEqualOrHigherThan(Rank.COLEADER)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_permission")));
            return;
        }

        // Get current chunk
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Attempt to claim the chunk
        ClaimResult result = api.claimChunk(faction.getId(), chunkX, chunkZ, true);
        handleClaimResult(player, result, chunkX, chunkZ);
    }

    public static void handleClaimResult(Player player, ClaimResult result, int chunkX, int chunkZ) {
        MineClans mineClans = MineClans.getInstance();
        ConfigWrapper messages = mineClans.getMessages();
        MineClansAPI api = mineClans.getAPI();
        String message = messages.getText(result.getMessagePath());
        ChunkCoordinate existingClaim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ);
        String ownerName = existingClaim != null ? api.getFactionName(existingClaim.getFactionId()) : "Unknown";

        message = message
                .replace("%faction%", ownerName)
                .replace("%x%", String.valueOf(chunkX))
                .replace("%z%", String.valueOf(chunkZ));

        switch (result) {
            case CLAIM_LIMIT_REACHED:
                Faction faction = api.getFaction(player);
                player.sendMessage(ChatColors.color(message
                        .replace("%current%", NumberUtil.formatPower(faction.getClaimedLandCount()))
                        .replace("%max%", NumberUtil.formatPower(faction.getPower()))));
                break;
            default:
                player.sendMessage(ChatColors.color(message));
                break;
        }
    }
}