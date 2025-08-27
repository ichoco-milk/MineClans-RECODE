package com.arkflame.mineclans.api.results.clan;

import lombok.Getter;

@Getter
public enum ClaimResult {
    SUCCESS("factions.claim.success"),
    ALREADY_CLAIMED("factions.claim.already_claimed"),
    FACTION_NOT_FOUND("factions.claim.not_in_faction"),
    CLAIM_LIMIT_REACHED("factions.claim.limit_reached"),
    NO_ADJACENT_CLAIM("factions.claim.no_adjacent"),
    NOT_RAIDABLE("factions.claim.not_raidable"),
    CHUNK_FACTION_GONE("factions.claim.success");

    private final String messagePath;

    ClaimResult(String messagePath) {
        this.messagePath = messagePath;
    }

    public boolean isSuccess() {
        return this == SUCCESS || this == CHUNK_FACTION_GONE;
    }
}