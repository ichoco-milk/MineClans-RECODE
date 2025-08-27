package com.arkflame.mineclans.api.results.action;

public record RallyResult(
    RallyResultType resultType
) {
    public enum RallyResultType {
        SUCCESS,
        IN_COOLDOWN,
        NO_RANK,
        NO_FACTION,
    }
}
