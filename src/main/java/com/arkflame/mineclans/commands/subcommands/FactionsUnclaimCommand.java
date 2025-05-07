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
        String worldName = chunk.getWorld().getName();

        // Check if chunk is claimed by this faction
        if (!api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ, worldName)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_claimed")));
            return;
        }

        ChunkCoordinate claim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ, worldName);
        if (!claim.getFactionId().equals(faction.getId())) {
            String ownerName = api.getFaction(claim.getFactionId()).getName();
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "claimed_by_other")
                    .replace("%faction%", ownerName)));
            return;
        }

        // Attempt to unclaim the chunk
        boolean success = api.getClaimedChunks().unclaimChunk(chunkX, chunkZ, worldName, true);
        
        if (success) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "success")
                    .replace("%x%", String.valueOf(chunkX))
                    .replace("%z%", String.valueOf(chunkZ))));
        } else {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "error")));
        }
    }
}