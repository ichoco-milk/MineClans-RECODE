package com.arkflame.mineclans.api.results.member;

import com.arkflame.mineclans.api.models.Faction;
import com.arkflame.mineclans.api.models.FactionPlayer;

public record JoinResult(
    JoinResultState state,
    Faction faction,
    FactionPlayer factionPlayer
) {
    public enum JoinResultState {
        SUCCESS,
        NULL_NAME,
        NO_FACTION,
        ALREADY_HAVE_FACTION, NOT_INVITED,
    }
}