package com.arkflame.mineclans.commands.subcommands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.menu.FactionsClaimsInventoryMenu;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.Paginator;

public class FactionsClaimsCommand {
    private static final String BASE_PATH = "factions.claims.";
    private static final int CLAIMS_PER_PAGE = 5;

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();

        if (!mineClans.getCfg().getBoolean("claims.enabled", true)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "disabled")));
            return;
        }

        // Check if player is in a faction
        Faction faction = api.getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_in_faction")));
            return;
        }

        // Get all claims for the faction
        Set<ChunkCoordinate> claims = api.getClaimedChunks().getClaimedChunks(faction.getId());
        int totalClaims = claims.size();

        if (totalClaims == 0) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_claims")));
            return;
        }

        // Convert set to list for indexed access
        List<ChunkCoordinate> claimsList = new ArrayList<>(claims);
        
        // Check if we should use the GUI menu or console output
        if (player.isConversing() || "true".equals(System.getProperty("mineclans.force_console_output"))) {
            // Console-style text output (original functionality)
            showTextOutput(player, claimsList, faction, args, mineClans, messages);
        } else {
            // Open the inventory menu
            FactionsClaimsInventoryMenu menu = new FactionsClaimsInventoryMenu(player, faction, claimsList);
            menu.open();
        }
    }
    
    /**
     * Show the original text-based claims list (for console or forced text mode)
     */
    private static void showTextOutput(Player player, List<ChunkCoordinate> claimsList, Faction faction, 
            ModernArguments args, MineClans mineClans, ConfigWrapper messages) {
        // Get current page (default to 1 if not specified)
        int page = 1;
        if (args.hasArg(1)) {
            try {
                page = args.getNumber(1);
            } catch (NumberFormatException e) {
                // Invalid page number, default to 1
            }
        }

        // Create paginator
        Paginator<ChunkCoordinate> paginator = new Paginator<>(claimsList, CLAIMS_PER_PAGE);
        int maxPage = paginator.getTotalPages();

        // Validate page number
        if (page < 1 || page > maxPage) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "invalid_page")
                    .replace("%max%", String.valueOf(maxPage))));
            return;
        }

        // Get current page of claims
        Collection<ChunkCoordinate> pageClaims = paginator.getPage(page);

        // Build header
        String header = ChatColors.color(messages.getText(BASE_PATH + "header")
                .replace("%faction%", faction.getName())
                .replace("%page%", String.valueOf(page))
                .replace("%max_page%", String.valueOf(maxPage))
                .replace("%total%", String.valueOf(claimsList.size())));

        // Build claims list
        StringBuilder claimsOutput = new StringBuilder();
        int chunkX = player.getLocation().getBlockX() >> 4;
        int chunkZ = player.getLocation().getBlockZ() >> 4;
        int index = (page - 1) * CLAIMS_PER_PAGE;

        for (ChunkCoordinate claim : pageClaims) {
            String claimFormat;
            boolean isCurrentChunk = (claim.getX() == chunkX && claim.getZ() == chunkZ
                    && claim.getWorldName().equals(player.getWorld().getName()))
                    && claim.getServerName().equals(MineClans.getServerId());

            if (isCurrentChunk) {
                claimFormat = messages.getText(BASE_PATH + "current_chunk_format");
            } else {
                claimFormat = messages.getText(BASE_PATH + "chunk_format");
            }

            claimsOutput.append(ChatColors.color(claimFormat
                    .replace("%index%", String.valueOf(index))
                    .replace("%x%", String.valueOf(claim.getX()))
                    .replace("%z%", String.valueOf(claim.getZ()))
                    .replace("%world%", claim.getWorldName())
                    .replace("%server%", claim.getServerName().substring(0, 5))))
                    .append("\n");
            index++;
        }

        // Send formatted message
        player.sendMessage(header);
        player.sendMessage(claimsOutput.toString());

        // Add footer with pagination help if there are multiple pages
        if (maxPage > 1) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "footer")
                    .replace("%command%", "/f claims")
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(maxPage))));
        }
    }
}