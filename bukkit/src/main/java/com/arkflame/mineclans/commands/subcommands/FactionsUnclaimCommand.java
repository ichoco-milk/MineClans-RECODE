package com.arkflame.mineclans.commands.subcommands;

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

import net.md_5.bungee.api.ChatColor;

public class FactionsUnclaimCommand {
    private static final String BASE_PATH = "factions.unclaim.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();

        if (!mineClans.getCfg().getBoolean("claims.enabled", true)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "disabled")));
            return;
        }

        if (player.hasPermission("mineclans.admin")) {
            int chunkX = player.getLocation().getBlockX() >> 4;
            int chunkZ = player.getLocation().getBlockZ() >> 4;
            String worldName = player.getLocation().getWorld().getName();
            api.getClaimedChunks().unclaimChunk(chunkX, chunkZ, worldName, true);
            player.sendMessage(ChatColor.RED + "Administrator forced unclaimed chunk!!!");
        }

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

        // Check if want to unclaim all
        if (args.hasArg(1) && args.getText(1).equalsIgnoreCase("all")) {
            api.getClaimedChunks().unclaimAllChunks(faction.getId());
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "unclaimed_all")));
            return;
        }

        // Get current chunk
        int chunkX = player.getLocation().getBlockX() >> 4;
        int chunkZ = player.getLocation().getBlockZ() >> 4;
        String worldName = player.getLocation().getWorld().getName();

        // Check if chunk is claimed by this faction
        if (!api.getClaimedChunks().isChunkClaimed(chunkX, chunkZ, worldName)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_claimed")));
            return;
        }

        ChunkCoordinate claim = api.getClaimedChunks().getChunkAt(chunkX, chunkZ, worldName);
        if (claim != null && !faction.getId().equals(claim.getFactionId())) {
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