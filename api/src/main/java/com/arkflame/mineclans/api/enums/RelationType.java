package com.arkflame.mineclans.api.enums;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum RelationType {
    ENEMY(ChatColor.RED),
    NEUTRAL(ChatColor.YELLOW),
    ALLY(ChatColor.AQUA),
    SAME_FACTION(ChatColor.GREEN);
    
    private final ChatColor color;

    RelationType(ChatColor color) {
        this.color = color;
    }

}
