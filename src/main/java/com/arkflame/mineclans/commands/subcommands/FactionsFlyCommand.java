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
import com.arkflame.mineclans.modernlib.utils.Players;

public class FactionsFlyCommand {
    public static final String BASE_PATH = "factions.fly.";

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
        if (!player.hasPermission("mineclans.fly")) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "no_permission_node")));
            return;
        }

        // Toggle flying status
        boolean newFlyingStatus = !factionPlayer.isFlying();
        factionPlayer.setFlying(newFlyingStatus);

        if (newFlyingStatus) {
            // Player enabled flying
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "enabled")));
            
            // Check if can currently use benefits
            if (benefitsManager.canUseRankBenefits(factionPlayer, player)) {
                Players.setFlying(player, true);
                player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "activated")));
            } else {
                player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "enabled_but_conditions_not_met")));
            }
        } else {
            // Player disabled flying
            factionPlayer.setFlying(false);
            Players.setFlying(player, false);
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "disabled")));
        }

        // Update status immediately
        benefitsManager.updateBenefits(player);
    }
}