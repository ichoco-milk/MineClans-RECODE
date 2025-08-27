package com.arkflame.mineclans.modernlib.menus.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.arkflame.mineclans.modernlib.menus.Menu;
import com.arkflame.mineclans.modernlib.menus.items.MenuItem;

public class MenuListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Menu) {
            Menu menu = (Menu) holder;
            event.setCancelled(true);

            int slot = event.getSlot();
            MenuItem menuItem = menu.getItem(slot);
            HumanEntity humanEntity = event.getWhoClicked();

            if (menuItem != null) {
                menuItem.onClick();
                menuItem.onClick(slot);
                if (humanEntity instanceof Player) {
                    menuItem.onClick((Player) humanEntity, slot);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Menu) {
            event.setCancelled(true);
        }
    }
}
