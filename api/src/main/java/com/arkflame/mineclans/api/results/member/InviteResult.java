package com.arkflame.mineclans.api.results.member;

import com.arkflame.mineclans.api.models.Faction;
import com.arkflame.mineclans.api.models.FactionPlayer;

public record InviteResult(
    InviteResultState state,
    FactionPlayer player,
    Faction faction
) {
    public InviteResult(InviteResultState state) {
        this(state, null, null);
    }

    public InviteResult(InviteResultState state, FactionPlayer player) {
        this(state, player, null);
    }

    public enum InviteResultState {
        SUCCESS,
        NO_FACTION,
        NO_PERMISSION,
        ALREADY_INVITED,
        MEMBER_EXISTS,
        PLAYER_NOT_FOUND,
    }
}
