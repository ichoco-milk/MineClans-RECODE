package com.arkflame.mineclans.api.results.misc;

public record FriendlyFireResult(
    FriendlyFireResultState state
) {
    public enum FriendlyFireResultState {
        ENABLED,
        DISABLED,
        NOT_IN_FACTION,
        ERROR, NO_PERMISSION
    }
}
