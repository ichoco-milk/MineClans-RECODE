package com.arkflame.mineclans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.menu.FactionsClaimsInventoryMenu;
import com.arkflame.mineclans.modernlib.utils.ChatColors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FactionsClaimsMenuListener implements Listener {
    private final MineClans plugin;
    private final Map<UUID, FactionsClaimsInventoryMenu> openMenus;

    public FactionsClaimsMenuListener(MineClans plugin) {
        this.plugin = plugin;
        this.openMenus = new ConcurrentHashMap<>();

        // Register listener with the plugin
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Register an open menu for a player
     */
    public void registerMenu(Player player, FactionsClaimsInventoryMenu menu) {
        openMenus.put(player.getUniqueId(), menu);
    }

    /**
     * Unregister a menu for a player
     */
    public void unregisterMenu(Player player) {
        openMenus.remove(player.getUniqueId());
    }

    /**
     * Handle inventory click events
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        // Check if player has an open claims menu
        FactionsClaimsInventoryMenu menu = openMenus.get(player.getUniqueId());
        if (menu == null) {
            return;
        }

        try {
            // Check if the clicked inventory is the player's inventory
            if (inventory == player.getInventory()) {
                event.setCancelled(true);
                return;
            }

            // Prevent taking items from the menu
            if (event.getView().getTitle().contains(ChatColors
                    .color(plugin.getMessages().getText("factions.claims.menu.title", "Claims").split("%")[0]))) {
                event.setCancelled(true);

                // Handle the click in the menu
                menu.handleClick(event.getRawSlot());
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // Check if player has an open claims menu
        FactionsClaimsInventoryMenu menu = openMenus.get(player.getUniqueId());
        if (menu == null) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Handle inventory close events
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        // Remove the menu from active menus when closed
        if (openMenus.containsKey(player.getUniqueId())) {
            unregisterMenu(player);
        }
    }

    public boolean hasMenu(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }
}