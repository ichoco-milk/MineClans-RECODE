package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.models.Faction;

public record RenameResult(
    Faction faction,
    RenameResultState state
) {
    public enum RenameResultState {
        SUCCESS,
        NOT_IN_FACTION,
        ALREADY_EXISTS, 
        NULL_NAME, 
        ERROR, 
        NO_PERMISSION,
        COOLDOWN,
        INVALID_NAME
    }
}
