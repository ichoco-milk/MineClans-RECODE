package com.arkflame.mineclans.api.results.action;

import com.arkflame.mineclans.api.models.Faction;
import com.arkflame.mineclans.api.models.FactionPlayer;

public record OpenChestResult(
    OpenChestResultType resultType,
    Faction faction,
    FactionPlayer player
) {
    public enum OpenChestResultType {
        SUCCESS,
        NOT_IN_FACTION,
        ERROR,
        NO_PERMISSION
    }
}
