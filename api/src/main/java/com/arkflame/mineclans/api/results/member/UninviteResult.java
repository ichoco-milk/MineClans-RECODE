package com.arkflame.mineclans.api.results.member;

public record UninviteResult(
    UninviteResultState state
) {
    public enum UninviteResultState {
        SUCCESS,
        NO_FACTION,
        NOT_INVITED, 
        PLAYER_NOT_FOUND, 
        NULL_NAME, 
        NO_PERMISSION,
    }
}
