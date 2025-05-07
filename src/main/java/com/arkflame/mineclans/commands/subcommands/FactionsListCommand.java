package com.arkflame.mineclans.commands.subcommands;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.utils.NumberUtil;
import com.arkflame.mineclans.utils.Paginator;

public class FactionsListCommand {
    private static final int FACTIONS_PER_PAGE = 10;

    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();

        // Get the page number from arguments, default to page 1 if not provided
        int page = 1;
        if (args.hasArg(1)) {
            try {
                page = Math.max(1, Integer.parseInt(args.getText(1)));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Get factions and their online member counts
        Map<Faction, Integer> factionCountMap = new HashMap<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Faction faction = MineClans.getInstance().getAPI().getFaction(onlinePlayer);
            if (faction != null) {
                factionCountMap.put(faction, factionCountMap.getOrDefault(faction, 0) + 1);
            }
        }

        // Sort factions by online member count (descending)
        List<Faction> sortedFactions = factionCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Create paginator
        Paginator<Faction> paginator = new Paginator<>(new HashSet<>(sortedFactions), FACTIONS_PER_PAGE);
        int totalPages = paginator.getTotalPages();

        // Validate page number
        if (page < 1 || page > totalPages) {
            player.sendMessage(messages.getText("factions.list.invalid_page")
                    .replace("%max_page%", String.valueOf(totalPages)));
            return;
        }

        // Display header
        player.sendMessage(messages.getText("factions.list.header")
                .replace("%current_page%", String.valueOf(page))
                .replace("%total_pages%", String.valueOf(totalPages)));

        // Display paginated factions
        Set<Faction> pageFactions = paginator.getPage(page);
        for (Faction faction : pageFactions) {
            int onlineCount = factionCountMap.getOrDefault(faction, 0);
            int totalMembers = faction.getMembers().size();
            String score = NumberUtil.formatScore(faction.getScore());
            int power = faction.getPower();
            int maxPower = faction.getMaxPower();

            player.sendMessage(messages.getText("factions.list.entry")
                    .replace("%faction%", faction.getName())
                    .replace("%online%", String.valueOf(onlineCount))
                    .replace("%total%", String.valueOf(totalMembers))
                    .replace("%level%", score)
                    .replace("%power%", String.valueOf(power))
                    .replace("%max_power%", String.valueOf(maxPower)));
        }

        // Display footer
        if (totalPages > 1) {
            player.sendMessage(messages.getText("factions.list.footer")
                    .replace("%current_page%", String.valueOf(page))
                    .replace("%total_pages%", String.valueOf(totalPages))
                    .replace("%command%", "/f list"));
        }
    }
}