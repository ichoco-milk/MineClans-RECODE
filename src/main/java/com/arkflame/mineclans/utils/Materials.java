package com.arkflame.mineclans.utils;

import org.bukkit.Material;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Materials {

    public static Set<Material> interactables = ConcurrentHashMap.newKeySet();
    static {
        // Signs
        interactables.add(Materials.get("SIGN_POST"));
        interactables.add(Materials.get("SIGN_WALL"));
        interactables.add(Materials.get("SIGN"));

        // Containers
        interactables.add(Materials.get("CHEST"));
        interactables.add(Materials.get("TRAPPED_CHEST"));
        interactables.add(Materials.get("FURNACE"));
        interactables.add(Materials.get("BURNING_FURNACE")); // 1.8 only
        interactables.add(Materials.get("HOPPER"));
        interactables.add(Materials.get("DISPENSER"));
        interactables.add(Materials.get("DROPPER"));
        interactables.add(Materials.get("BARREL")); // 1.14+
        interactables.add(Materials.get("BLAST_FURNACE")); // 1.14+
        interactables.add(Materials.get("SMOKER")); // 1.14+
        interactables.add(Materials.get("SHULKER_BOX")); // 1.11+
        interactables.add(Materials.get("ENDER_CHEST"));

        // Crafting/Utility
        interactables.add(Materials.get("ANVIL"));
        interactables.add(Materials.get("CRAFTING_TABLE"));
        interactables.add(Materials.get("ENCHANTING_TABLE"));
        interactables.add(Materials.get("GRINDSTONE")); // 1.14+
        interactables.add(Materials.get("STONECUTTER")); // 1.14+
        interactables.add(Materials.get("SMITHING_TABLE")); // 1.14+
        interactables.add(Materials.get("CARTOGRAPHY_TABLE")); // 1.14+
        interactables.add(Materials.get("LOOM")); // 1.14+
        interactables.add(Materials.get("FLETCHING_TABLE")); // 1.14+

        // Redstone/Mechanisms
        interactables.add(Materials.get("LEVER"));
        interactables.add(Materials.get("BUTTON")); // legacy
        interactables.add(Materials.get("STONE_BUTTON"));
        interactables.add(Materials.get("WOOD_BUTTON"));
        interactables.add(Materials.get("TRIPWIRE_HOOK"));
        interactables.add(Materials.get("DAYLIGHT_DETECTOR"));
        interactables.add(Materials.get("NOTE_BLOCK"));
        interactables.add(Materials.get("COMPARATOR"));
        interactables.add(Materials.get("REPEATER"));

        // Doors & Trapdoors
        interactables.add(Materials.get("IRON_DOOR"));
        interactables.add(Materials.get("WOODEN_DOOR")); // legacy
        interactables.add(Materials.get("OAK_DOOR"));
        interactables.add(Materials.get("BIRCH_DOOR"));
        interactables.add(Materials.get("SPRUCE_DOOR"));
        interactables.add(Materials.get("JUNGLE_DOOR"));
        interactables.add(Materials.get("ACACIA_DOOR"));
        interactables.add(Materials.get("DARK_OAK_DOOR"));

        interactables.add(Materials.get("TRAP_DOOR")); // legacy
        interactables.add(Materials.get("OAK_TRAPDOOR"));
        interactables.add(Materials.get("IRON_TRAPDOOR"));

        // Gates
        interactables.add(Materials.get("FENCE_GATE"));
        interactables.add(Materials.get("OAK_FENCE_GATE"));
        interactables.add(Materials.get("BIRCH_FENCE_GATE"));
        interactables.add(Materials.get("SPRUCE_FENCE_GATE"));
        interactables.add(Materials.get("JUNGLE_FENCE_GATE"));
        interactables.add(Materials.get("ACACIA_FENCE_GATE"));
        interactables.add(Materials.get("DARK_OAK_FENCE_GATE"));

        // Beds
        interactables.add(Materials.get("BED")); // legacy
        interactables.add(Materials.get("RED_BED")); // 1.12+
        interactables.add(Materials.get("WHITE_BED"));
        interactables.add(Materials.get("BLUE_BED"));
        interactables.add(Materials.get("YELLOW_BED"));
        interactables.add(Materials.get("GREEN_BED"));
        interactables.add(Materials.get("BLACK_BED"));

        // Bells, Lecterns (1.14+)
        interactables.add(Materials.get("BELL"));
        interactables.add(Materials.get("LECTERN"));
    }
    private Materials() {}

    /**
     * Attempts to get the first valid Material from a list of names.
     * If no valid Material is found, returns Material.AIR.
     *
     * @param names One or more String names of the Material to retrieve.
     * @return The first found Material, or Material.AIR if none are found.
     */
    public static Material get(String... names) {
        for (String name : names) {
            Material material = Material.getMaterial(name.toUpperCase());
            if (material != null) {
                return material;
            }
        }
        // Return Material.AIR as a default if no valid Material was found
        return Material.AIR;
    }

    public static Material get(List<String> names) {
        return get(names.toArray(new String[0]));
    }

    public static boolean is(Material type, String... names) {
        for (String name : names) {
            Material material = get(name.toUpperCase());
            if (material != null && type == material) {
                return true;
            }
        }
        return false;
    }
}
