package com.arkflame.mineclans.api.results.clan;

import com.arkflame.mineclans.api.enums.RelationType;
import com.arkflame.mineclans.api.models.Faction;

public record SetRelationResult(
    SetRelationResultState state,
    Faction faction,
    Faction otherFaction,
    RelationType relation,
    RelationType otherRelation
) {
    public enum SetRelationResultState {
        SUCCESS,
        INVALID_RELATION_TYPE,
        NO_FACTION,
        OTHER_FACTION_NOT_FOUND,
        SAME_FACTION,
        ALREADY_RELATION
    }
}
