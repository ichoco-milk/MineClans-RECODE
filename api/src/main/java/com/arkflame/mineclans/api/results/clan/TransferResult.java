package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.models.Faction;

public record TransferResult(
    TransferResultState state,
    Faction faction
) {
    public enum TransferResultState {
        SUCCESS,
        NULL_NAME,
        NO_FACTION,
        NOT_OWNER,
        MEMBER_NOT_FOUND
    }
}
