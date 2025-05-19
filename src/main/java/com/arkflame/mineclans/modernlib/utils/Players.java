package com.arkflame.mineclans.modernlib.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;

public class Players {
    public static void setFlying(Player player, boolean flying) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(MineClans.getInstance(), () -> setFlying(player, flying));
            return;
        }
        if (player.getAllowFlight() != flying) {
            player.setAllowFlight(flying);
        }
        if (player.isFlying() != flying) {
            player.setFlying(flying);
        }
    }

    public static void clearInventory(Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
    }

    public static void sendMessage(Player player, List<String> textList) {
        for (String text : textList) {
            player.sendMessage(text);
        }
    }

    public static void heal(Player player) {
        player.setHealth(20D);
        player.setFoodLevel(20);
    }
}
