package com.arkflame.mineclans.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class FactionFriendlyFireListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Entity) {
                damager = (Entity) shooter;
            }
        }
        Entity entity = event.getEntity();
        if (!(damager instanceof Player) || !(entity instanceof Player)) {
            return;
        }

        Player attacker = (Player) damager;
        Player victim = (Player) entity;

        FactionPlayer attackerFP = MineClans.getInstance().getAPI().getFactionPlayer(attacker.getUniqueId());
        FactionPlayer victimFP = MineClans.getInstance().getAPI().getFactionPlayer(victim.getUniqueId());

        if (attackerFP == null || victimFP == null || attackerFP.getFaction() == null || victimFP.getFaction() == null) {
            return;
        }

        Faction attackerFaction = attackerFP.getFaction();
        Faction victimFaction = victimFP.getFaction();

        // Check if both players are in the same faction and friendly fire is disabled
        if (attackerFaction.equals(victimFaction) && !attackerFaction.isFriendlyFire()) {
            event.setCancelled(true);
            attacker.sendMessage(MineClans.getInstance().getMessages().getText("factions.friendly_fire.cannot_attack"));
        }
    }
}
