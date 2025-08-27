package com.arkflame.mineclans.api.results.member;

public record AddEventsWonResult(
    AddEventsWonResultType type
) {
    public enum AddEventsWonResultType {
        SUCCESS, NO_FACTION, PLAYER_NOT_FOUND
    }
}