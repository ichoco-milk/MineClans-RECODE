package com.arkflame.mineclans.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.ChunkCoordinate;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.LocationData;
import com.arkflame.mineclans.utils.Materials;

public class FactionsClaimsInventoryMenu {
    private static final String BASE_PATH = "factions.claims.menu.";
    private static final int MENU_SIZE = 27; // 3 rows
    private static final int CLAIMS_PER_PAGE = 5;

    // Positions in inventory
    private static final int PREV_PAGE_SLOT = 10; // Left center
    private static final int NEXT_PAGE_SLOT = 16; // Right center
    private static final int[] CLAIM_SLOTS = { 11, 12, 13, 14, 15 }; // Center positions

    private final Player player;
    private final Faction faction;
    private final List<ChunkCoordinate> claims;
    private final int totalPages;
    private int currentPage;
    private Inventory menu;

    public FactionsClaimsInventoryMenu(Player player, Faction faction, List<ChunkCoordinate> claims) {
        this.player = player;
        this.faction = faction;
        this.claims = claims;
        this.totalPages = (int) Math.ceil((double) claims.size() / CLAIMS_PER_PAGE);
        this.currentPage = 1;
    }

    public void openInventory(Player player, Inventory inventory) {
        if (menu == null) {
            return;
        }
        MineClans.runSync(() -> {
            player.closeInventory();
            MineClans.getInstance().getClaimsMenuListener().registerMenu(player, this);
            player.openInventory(menu);
        });
    }

    /**
     * Open the claims menu for the player
     */
    public void open() {
        createMenu();
        openInventory(player, menu);
    }

    /**
     * Create the inventory menu with all items
     */
    private void createMenu() {
        MineClans mineClans = MineClans.getInstance();
        ConfigWrapper messages = mineClans.getMessages();

        String menuTitle = ChatColors.color(messages.getText(BASE_PATH + "title")
                .replace("%faction%", faction.getDisplayName())
                .replace("%page%", String.valueOf(currentPage))
                .replace("%max_page%", String.valueOf(totalPages)));

        // If title is too long, truncate it to prevent errors
        if (menuTitle.length() > 32) {
            menuTitle = menuTitle.substring(0, 32);
        }

        menu = mineClans.getServer().createInventory(null, MENU_SIZE, menuTitle);

        // Add page navigation buttons if needed
        if (currentPage > 1) {
            menu.setItem(PREV_PAGE_SLOT,
                    createNavigationItem(Material.ARROW, messages.getText(BASE_PATH + "previous_page")));
        }

        if (currentPage < totalPages) {
            menu.setItem(NEXT_PAGE_SLOT,
                    createNavigationItem(Material.ARROW, messages.getText(BASE_PATH + "next_page")));
        }

        // Add claim items
        int startIndex = (currentPage - 1) * CLAIMS_PER_PAGE;
        int endIndex = Math.min(startIndex + CLAIMS_PER_PAGE, claims.size());

        for (int i = startIndex; i < endIndex; i++) {
            ChunkCoordinate claim = claims.get(i);
            int slotIndex = i - startIndex;

            boolean isCurrentChunk = isPlayerInChunk(player, claim);

            ItemStack claimItem = createClaimItem(claim, i, isCurrentChunk, messages);
            menu.setItem(CLAIM_SLOTS[slotIndex], claimItem);
        }

        // Fill empty spaces with glass panes
        ItemStack filler = createFillerItem();
        for (int i = 0; i < MENU_SIZE; i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, filler);
            }
        }
    }

    /**
     * Create an item representing a claim
     */
    private ItemStack createClaimItem(ChunkCoordinate claim, int index, boolean isCurrentChunk,
            ConfigWrapper messages) {
        Material material = isCurrentChunk ? Materials.get(messages.getTextList(BASE_PATH + "current_chunk_material"))
                : Materials.get(messages.getTextList(BASE_PATH + "chunk_material"));
        if (material != null) {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String displayName = ChatColors.color(messages.getText(BASE_PATH + "claim_item_name")
                        .replace("%index%", String.valueOf(index))
                        .replace("%x%", String.valueOf(claim.getX()))
                        .replace("%z%", String.valueOf(claim.getZ())));

                meta.setDisplayName(displayName);

                List<String> lore = new ArrayList<>();
                for (String loreLine : messages.getStringList(BASE_PATH + "claim_item_lore")) {
                    lore.add(ChatColors.color(loreLine
                            .replace("%index%", String.valueOf(index))
                            .replace("%x%", String.valueOf(claim.getX()))
                            .replace("%z%", String.valueOf(claim.getZ()))
                            .replace("%world%", claim.getWorldName())
                            .replace("%server%", claim.getServerName().substring(0, 5))));
                }

                // Add teleport instruction
                lore.add(ChatColors.color(messages.getText(BASE_PATH + "click_to_teleport")));

                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            return item;
        }
        return null;
    }

    /**
     * Create a navigation button item
     */
    private ItemStack createNavigationItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColors.color(name));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create filler item for empty slots
     */
    private ItemStack createFillerItem() {
        ItemStack item = new ItemStack(Materials.get("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE"), 1, (short) 7);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Check if player is in the given chunk
     */
    private boolean isPlayerInChunk(Player player, ChunkCoordinate claim) {
        int chunkX = player.getLocation().getBlockX() >> 4;
        int chunkZ = player.getLocation().getBlockZ() >> 4;
        return claim.getX() == chunkX &&
                claim.getZ() == chunkZ &&
                claim.getWorldName().equals(player.getWorld().getName()) &&
                claim.getServerName().equals(MineClans.getServerId());
    }

    /**
     * Navigate to the next page
     */
    public void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            createMenu();
            openInventory(player, menu);
        }
    }

    /**
     * Navigate to the previous page
     */
    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            createMenu();
            openInventory(player, menu);
        }
    }

    /**
     * Teleport player to the selected claim
     */
    public void teleportToClaim(int slotIndex) {
        int claimIndex = (currentPage - 1) * CLAIMS_PER_PAGE;

        for (int i = 0; i < CLAIM_SLOTS.length; i++) {
            if (CLAIM_SLOTS[i] == slotIndex) {
                claimIndex += i;
                break;
            }
        }

        if (claimIndex >= 0 && claimIndex < claims.size()) {
            teleportToClaimIndex(claimIndex);
        }
    }

    /**
     * Teleport to a claim by its index
     */
    private void teleportToClaimIndex(int claimIndex) {
        MineClans mineClans = MineClans.getInstance();
        ConfigWrapper messages = mineClans.getMessages();

        ChunkCoordinate targetClaim = claims.get(claimIndex);
        String worldName = targetClaim.getWorldName();

        if (worldName == null || mineClans.getServer().getWorld(worldName) == null) {
            player.sendMessage(ChatColors.color(messages.getText("factions.tpclaim.invalid_world")));
            return;
        }

        // Close inventory first
        player.closeInventory();

        // Calculate teleport location (center of the chunk)
        MineClans.runSync(() -> {
            Chunk chunk = mineClans.getServer().getWorld(worldName).getChunkAt(targetClaim.getX(), targetClaim.getZ());
            MineClans.runAsync(() -> {
                Location targetLocation = new Location(
                        chunk.getWorld(),
                        chunk.getX() * 16 + 8, // Center X of chunk
                        chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + 8, chunk.getZ() * 16 + 8) + 1, // Safe Y
                                                                                                               // position
                        chunk.getZ() * 16 + 8 // Center Z of chunk
                );

                // Schedule teleport with warmup
                int warmup = mineClans.getCfg().getInt("claims.warmup", 10);
                player.sendMessage(ChatColors.color(messages.getText("factions.tpclaim.teleporting")
                        .replace("{time}", String.valueOf(warmup))
                        .replace("{claim}", String.valueOf(claimIndex))
                        .replace("{x}", String.valueOf(targetClaim.getX()))
                        .replace("{z}", String.valueOf(targetClaim.getZ()))));

                // Use the same teleport scheduler as the home command
                mineClans.getTeleportScheduler().schedule(player, new LocationData(targetLocation, null), warmup);
            });
        });
    }

    /**
     * Handle inventory click events
     */
    public void handleClick(int slot) {
        if (slot == PREV_PAGE_SLOT && currentPage > 1) {
            previousPage();
        } else if (slot == NEXT_PAGE_SLOT && currentPage < totalPages) {
            nextPage();
        } else {
            for (int i = 0; i < CLAIM_SLOTS.length; i++) {
                if (CLAIM_SLOTS[i] == slot) {
                    teleportToClaim(slot);
                    break;
                }
            }
        }
    }
}