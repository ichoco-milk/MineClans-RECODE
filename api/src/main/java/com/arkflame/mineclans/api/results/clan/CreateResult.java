package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.models.Faction;

public record CreateResult(
    Faction faction,
    CreateResultState state
) {
    public enum CreateResultState {
        SUCCESS,
        NULL_NAME,
        FACTION_EXISTS,
        ALREADY_HAVE_FACTION, ERROR, INVALID_NAME,
    }
}