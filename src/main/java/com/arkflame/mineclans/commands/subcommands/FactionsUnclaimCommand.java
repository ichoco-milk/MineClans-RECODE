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

public class FactionsUnclaimCommand {
    private static final String BASE_PATH = "factions.unclaim.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();

        // Debug: Start of command execution
        mineClans.getLogger().info("[UnclaimCommand] Unclaim command executed by " + player.getName());

        // Check if player is in a faction
        Faction faction = api.getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_in_faction")));
            mineClans.getLogger().info("[UnclaimCommand] Player not in a faction");
            return;
        }

        // Check player permissions
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (!factionPlayer.getRank().isEqualOrHigherThan(Rank.COLEADER)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_permission")));
            mineClans.getLogger().info("[UnclaimCommand] Player lacks permission to unclaim");
            return;
        }

        // Get current chunk
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Debug: Chunk coordinates
        mineClans.getLogger().info("[UnclaimCommand] Attempting to unclaim chunk at X:" + chunkX + " Z:" + chunkZ);

        // Check if chunk is claimed by this faction
        if (!api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_claimed")));
            mineClans.getLogger().info("[UnclaimCommand] Chunk not claimed by anyone");
            return;
        }

        ChunkCoordinate claim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ);
        if (!claim.getFactionId().equals(faction.getId())) {
            String ownerName = api.getFaction(claim.getFactionId()).getName();
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "claimed_by_other")
                    .replace("%faction%", ownerName)));
            mineClans.getLogger().info("[UnclaimCommand] Chunk claimed by other faction: " + ownerName);
            return;
        }

        // Attempt to unclaim the chunk
        boolean success = api.getClaimedChunks().unclaimChunk(faction.getId(), chunkX, chunkZ, true);
        
        if (success) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "success")
                    .replace("%x%", String.valueOf(chunkX))
                    .replace("%z%", String.valueOf(chunkZ))));
            mineClans.getLogger().info("[UnclaimCommand] Successfully unclaimed chunk for faction " + faction.getName());
        } else {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "error")));
            mineClans.getLogger().warning("[UnclaimCommand] Failed to unclaim chunk for unknown reason");
        }
    }
}