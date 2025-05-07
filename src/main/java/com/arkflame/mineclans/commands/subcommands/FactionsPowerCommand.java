package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsPowerCommand {
    private static final String BASE_PATH = "factions.power.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClans = MineClans.getInstance();
        MineClansAPI api = mineClans.getAPI();
        ConfigWrapper messages = mineClans.getMessages();

        // Check if looking up another player's power
        if (args.hasArg(1)) {
            String targetName = args.getText(1);
            showOtherPlayerPower(player, api, messages, targetName);
            return;
        }

        // Show own power
        FactionPlayer factionPlayer = api.getFactionPlayer(player);
        if (factionPlayer == null) {
            player.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "error")));
            return;
        }

        String formattedMessage = messages.getText(BASE_PATH + "self")
                .replace("%power%", NumberUtil.formatBalance(factionPlayer.getPower()))
                .replace("%max_power%", NumberUtil.formatBalance(factionPlayer.getMaxPower()));
        player.sendMessage(ChatColors.color(formattedMessage));
    }

    private static void showOtherPlayerPower(Player sender, MineClansAPI api, ConfigWrapper messages, String targetName) {
        // Try to find player by name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
        if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "invalid_player")));
            return;
        }

        FactionPlayer factionPlayer = api.getFactionPlayer(targetPlayer.getUniqueId());
        if (factionPlayer == null) {
            sender.sendMessage(ChatColors.color(messages.getText(BASE_PATH + "not_in_faction_other")
                    .replace("%player%", targetName)));
            return;
        }

        String formattedMessage;
        if (sender.getUniqueId().equals(targetPlayer.getUniqueId())) {
            // Same as showing own power
            formattedMessage = messages.getText(BASE_PATH + "self")
                    .replace("%power%", NumberUtil.formatBalance(factionPlayer.getPower()))
                    .replace("%max_power%", NumberUtil.formatBalance(factionPlayer.getMaxPower()));
        } else {
            // Show other player's power
            formattedMessage = messages.getText(BASE_PATH + "other")
                    .replace("%player%", targetName)
                    .replace("%power%", NumberUtil.formatBalance(factionPlayer.getPower()))
                    .replace("%max_power%", NumberUtil.formatBalance(factionPlayer.getMaxPower()));
        }

        sender.sendMessage(ChatColors.color(formattedMessage));
    }
}