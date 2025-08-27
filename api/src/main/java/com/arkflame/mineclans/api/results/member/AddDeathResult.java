package com.arkflame.mineclans.api.results.member;

import com.arkflame.mineclans.api.models.FactionPlayer;

public record AddDeathResult(
    AddDeathResultState state,
    FactionPlayer player
) {
    public enum AddDeathResultState {
        SUCCESS,
        ERROR, 
        NO_PLAYER
    }
}
