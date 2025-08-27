package com.arkflame.mineclans.api.results.chat;

import com.arkflame.mineclans.api.models.Faction;

public record DiscordResult(
    DiscordResultState state,
    Faction faction,
    String discordLink  // Optional additional field for the Discord link, if applicable
) {
    // Constructor for state and faction without discord link
    public DiscordResult(DiscordResultState state, Faction faction) {
        this(state, faction, null);
    }

    // Constructor for state only
    public DiscordResult(DiscordResultState state) {
        this(state, null, null);
    }

    public enum DiscordResultState {
        SUCCESS,
        NO_PERMISSION,
        NO_FACTION,
        INVALID_DISCORD_LINK,
        ERROR
    }
}
