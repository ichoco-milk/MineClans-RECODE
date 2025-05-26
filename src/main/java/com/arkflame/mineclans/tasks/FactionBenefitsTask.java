package com.arkflame.mineclans.tasks;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.MineClans;

public class FactionBenefitsTask implements Runnable {
    private Collection<UUID> toUpdate = ConcurrentHashMap.newKeySet();
    private boolean updating = false;

    public void scheduleUpdate(UUID uuid) {
        this.toUpdate.add(uuid);
    }

    @Override
    public void run() {
        if (updating || toUpdate.isEmpty())
            return;
        try {
            updating = true;
            for (UUID uuid : toUpdate) {
                MineClans.getInstance().getFactionBenefitsManager().updateBenefits(uuid);
            }
            toUpdate.clear();
        } finally {
            updating = false;
        }
    }

    public void register() {
        MineClans.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(MineClans.getInstance(), this, 20, 20);
    }
}
