package com.arkflame.mineclans.api.results.chat;

import com.arkflame.mineclans.api.models.Faction;
import com.arkflame.mineclans.api.models.FactionPlayer;

public record FactionChatResult(
    FactionChatState state,
    String message,
    Faction faction,
    FactionPlayer factionPlayer
) {
    public enum FactionChatState {
        SUCCESS,
        NOT_IN_FACTION,
        ERROR
    }
}
