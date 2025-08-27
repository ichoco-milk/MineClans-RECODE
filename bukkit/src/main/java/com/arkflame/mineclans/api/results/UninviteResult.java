package com.arkflame.mineclans.api.results;

public class UninviteResult {
    private UninviteResultState state;

    // Constructor
    public UninviteResult(UninviteResultState state) {
        this.state = state;
    }

    // Getter for UninviteResultState
    public UninviteResultState getState() {
        return state;
    }

    public enum UninviteResultState {
        SUCCESS,
        NO_FACTION,
        NOT_INVITED, 
        PLAYER_NOT_FOUND, 
        NULL_NAME, 
        NO_PERMISSION,
    }
}
