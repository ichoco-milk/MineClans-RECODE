package com.arkflame.mineclans.enums;

import org.bukkit.ChatColor;

public enum RelationType {
    ENEMY(ChatColor.RED),
    NEUTRAL(null),
    ALLY(ChatColor.AQUA),
    SAME_FACTION(ChatColor.GREEN);
    
    private final ChatColor color;
    RelationType(ChatColor color) {
        this.color = color;
    }
    public ChatColor getColor() {
        return color;
    }
}
