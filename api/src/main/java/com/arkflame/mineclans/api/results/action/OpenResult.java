package com.arkflame.mineclans.api.results.action;

import com.arkflame.mineclans.api.models.Faction;

public record OpenResult(
    OpenResultState state,
    Faction faction,
    boolean open
) {

    public OpenResult(OpenResultState state) {
        this(state, null, false);
    }

    public enum OpenResultState {
        SUCCESS,
        NO_FACTION,
        NO_PERMISSION,
    }
}