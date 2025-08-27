package com.arkflame.mineclans.api.results.member;

public record AddKillResult(
    AddKillResultType type
) {
    public enum AddKillResultType {
        SUCCESS,
        PLAYER_NOT_FOUND,
        SAME_FACTION,
        ALREADY_KILLED, NO_FACTION
    }
}
