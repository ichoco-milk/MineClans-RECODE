package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.managers.FactionBenefitsManager;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;

public class FactionsGodCommand {
    private static final String BASE_PATH = "factions.god.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();
        FactionBenefitsManager benefitsManager = mineClans.getFactionBenefitsManager();

        // Check if player is in a faction
        Faction faction = api.getFaction(player);
        if (faction == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_in_faction")));
            return;
        }

        // Check player permissions - require minimum rank
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (!factionPlayer.getRank().isEqualOrHigherThan(Rank.MEMBER)) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_permission")));
            return;
        }

        // Check if player has permission node
        if (!player.hasPermission("mineclans.god")) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_permission_node")));
            return;
        }

        // Toggle god mode status
        boolean newGodStatus = !factionPlayer.isGodMode();
        factionPlayer.setGodMode(newGodStatus);

        if (newGodStatus) {
            // Player enabled god mode
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "enabled")));
            
            // Check if can currently use benefits
            if (benefitsManager.canUseRankBenefits(player)) {
                factionPlayer.setCanReceiveDamage(false);
                player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "activated")));
            } else {
                player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "enabled_but_conditions_not_met")));
            }
        } else {
            factionPlayer.setGodMode(false);
            factionPlayer.setCanReceiveDamage(true);
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "disabled")));
        }

        // Update status immediately
        benefitsManager.updateRankBenefitsStatus(player);
    }
}