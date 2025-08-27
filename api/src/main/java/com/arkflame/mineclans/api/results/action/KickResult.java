package com.arkflame.mineclans.api.results.action;

import com.arkflame.mineclans.api.models.Faction;
import com.arkflame.mineclans.api.models.FactionPlayer;

public record KickResult(
    KickResultType state,
    Faction faction,
    FactionPlayer factionPlayer
) {
    public enum KickResultType {
        SUCCESS,
        NOT_IN_FACTION,
        NOT_MODERATOR,
        PLAYER_NOT_FOUND,
        SUPERIOR_RANK, 
        NOT_YOURSELF, 
        FACTION_OWNER, 
        NO_FACTION, DIFFERENT_FACTION, ALREADY_KICKED, ERROR;
    }
}