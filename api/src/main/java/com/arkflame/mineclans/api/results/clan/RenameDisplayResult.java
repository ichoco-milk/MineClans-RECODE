package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.models.Faction;

public record RenameDisplayResult(
    Faction faction,
    RenameDisplayResultState state
) {
    public enum RenameDisplayResultState {
        SUCCESS,
        NOT_IN_FACTION,
        DIFFERENT_NAME, 
        NULL_NAME, ERROR, INVALID_NAME
    }
}
