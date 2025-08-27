package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.enums.Rank;

public record RankChangeResult(
    RankChangeResultType resultType,
    Rank rank
) {
    public enum RankChangeResultType {
        SUCCESS,
        PLAYER_NOT_FOUND,
        NOT_IN_FACTION,
        NO_PERMISSION,
        SUPERIOR_RANK, 
        CANNOT_PROMOTE,
        CANNOT_DEMOTE, 
        CANNOT_PROMOTE_TO_LEADER,
    }
}
