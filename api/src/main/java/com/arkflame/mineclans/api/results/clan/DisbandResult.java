package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.models.Faction;

public record DisbandResult(
    DisbandResultState state,
    Faction faction
) {
    public enum DisbandResultState {
        SUCCESS,
        NO_PERMISSION,
        NO_FACTION,
    }
}