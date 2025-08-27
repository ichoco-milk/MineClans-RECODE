package com.arkflame.mineclans.api.results.action;

public record FocusResult(
    FocusResultType type
) {
    public enum FocusResultType {
        SUCCESS,
        NOT_IN_FACTION,
        FACTION_NOT_FOUND,
        NO_PERMISSION, 
        SAME_FACTION
    }
}
