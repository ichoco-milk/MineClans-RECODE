package com.arkflame.mineclans.api.results.chat;

import com.arkflame.mineclans.api.models.Faction;

public record AnnouncementResult(
    AnnouncementResultState state,
    Faction faction,
    String announcement // Optional additional field for the Discord link, if applicable
) {

    // Constructor for state and faction without discord link
    public AnnouncementResult(AnnouncementResultState state, Faction faction) {
        this(state, faction, null);
    }

    // Constructor for state only
    public AnnouncementResult(AnnouncementResultState state) {
        this(state, null, null);
    }

    // Getters
    public AnnouncementResultState getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }

    public String getAnnouncement() {
        return announcement;
    }

    // Enum for the possible states of setting Discord
    public enum AnnouncementResultState {
        SUCCESS,
        NO_PERMISSION,
        NO_FACTION,
        NO_ANNOUNCEMENT,
        ERROR
    }
}
