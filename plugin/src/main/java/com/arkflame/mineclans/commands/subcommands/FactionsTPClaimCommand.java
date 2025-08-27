package com.arkflame.mineclans.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.LocationData;

public class FactionsTPClaimCommand {
    private static final String BASE_PATH = "factions.tpclaim.";

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

        // Get player's faction claims
        List<ChunkCoordinate> claims = new ArrayList<>(api.getClaimedChunks().getClaimedChunks(faction.getId()));

        if (claims == null || claims.isEmpty()) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_claims")));
            return;
        }

        final int claimIndex = args.getNumber(1);
        if (claimIndex < 0 || claimIndex >= claims.size()) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "invalid_claim_number")
                    .replace("{max}", String.valueOf(claims.size()))));
            return;
        }

        // Get the target claim
        ChunkCoordinate targetClaim = claims.get(claimIndex);

        // Get world of the claim
        String worldName = targetClaim.getWorldName();
        if (worldName == null || mineClans.getServer().getWorld(worldName) == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "invalid_world")));
            return;
        }

        // Calculate teleport location (center of the chunk)
        MineClans.runSync(() -> {
            Chunk chunk = mineClans.getServer().getWorld(worldName).getChunkAt(targetClaim.getX(), targetClaim.getZ());
            MineClans.runAsync(() -> {
                Location targetLocation = new Location(
                        chunk.getWorld(),
                        chunk.getX() * 16 + 8, // Center X of chunk
                        chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + 8, chunk.getZ() * 16 + 8) + 1, // Safe Y
                                                                                                               // position
                        chunk.getZ() * 16 + 8 // Center Z of chunk
                );

                // Schedule teleport with warmup
                int warmup = mineClans.getCfg().getInt("claims.warmup", 10);
                player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "teleporting")
                        .replace("{time}", String.valueOf(warmup))
                        .replace("{claim}", String.valueOf(claimIndex))
                        .replace("{x}", String.valueOf(targetClaim.getX()))
                        .replace("{z}", String.valueOf(targetClaim.getZ()))));

                // Use the same teleport scheduler as the home command
                mineClans.getTeleportScheduler().schedule(player, new LocationData(targetLocation, null), warmup);
            });
        });
    }

    public static void showHelp(Player player) {
        MineClans mineClans = MineClans.getInstance();
        ConfigWrapper messages = mineClans.getMessages();

        player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "help.title")));
        player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "help.usage")));
        player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "help.description")));
    }
}