package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.action.KickResult;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.utils.Titles;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

public class FactionsLeaveCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        KickResult leaveResult = MineClans.getInstance().getAPI().kick(null, player.getName());
        Faction faction = leaveResult.getFaction();
        String basePath = "factions.leave.";

        switch (leaveResult.getState()) {
            case FACTION_OWNER:
                player.sendMessage(messages.getText(basePath + "faction_owner"));
                break;
            case NO_FACTION:
                player.sendMessage(messages.getText(basePath + "no_faction"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText("factions.leave.title").replace("%faction%", faction.getDisplayName()),
                        messages.getText("factions.leave.subtitle").replace("%faction%", faction.getDisplayName()),
                        10, 20, 10);
                MelodyUtil.playMelody(MineClans.getInstance(), player, Melody.FACTION_LEAVE_MELODY);
                player.sendMessage(messages.getText(basePath + "success"));
                faction.sendMessage(messages.getText(basePath + "faction_left").replace("%player%", player.getName()));
                break;
            default:
                break;
        }
    }
}
