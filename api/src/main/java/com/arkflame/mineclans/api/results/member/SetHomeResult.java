package com.arkflame.mineclans.api.results.member;

public record SetHomeResult(
    SetHomeResultState state
) {
    public enum SetHomeResultState {
        SUCCESS, NOT_IN_FACTION, ERROR, NO_PERMISSION, AT_ENEMY_CLAIM
    }
}
