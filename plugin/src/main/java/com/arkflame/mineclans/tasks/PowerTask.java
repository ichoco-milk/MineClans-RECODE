package com.arkflame.mineclans.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;

public class PowerTask implements Runnable {
    private static int POWER_UPDATE_INTERVAL = 20 * 60 * 2; // 2 minutes
    private static double POWER_PER_HOUR = 4; // Power to give per hour

    public static double getPowerPerUpdate(Player player) {
        double rankMultiplier = MineClans.getInstance().getPowerMultiplier(player);
        double negativeMultiplier = hasNegativePower(player) ? 2 : 1;
        double multiplier = (double) POWER_UPDATE_INTERVAL / 20 / 60 / 60;
        return POWER_PER_HOUR * multiplier * rankMultiplier * negativeMultiplier;
    }

    private static boolean hasNegativePower(Player player) {
        return MineClans.getInstance().getAPI().getFactionPlayer(player).getPower() < 0;
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MineClans.getInstance().getAPI().updatePower(player, getPowerPerUpdate(player), true);
        }
    }

    public static void start() {
        MineClans.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(MineClans.getInstance(),
                new PowerTask(), POWER_UPDATE_INTERVAL, POWER_UPDATE_INTERVAL);
    }
}
