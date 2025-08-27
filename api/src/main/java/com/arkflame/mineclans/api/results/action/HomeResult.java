package com.arkflame.mineclans.api.results.action;


import com.arkflame.mineclans.api.data.LocationData;

public record HomeResult(
    HomeResultState state,
    LocationData homeLocation
) {
    public HomeResult(HomeResultState state) {
        this(state, null);
    }

    public HomeResultState getState() {
        return state;
    }

    public LocationData getHomeLocation() {
        return homeLocation;
    }
    
    public enum HomeResultState {
        NOT_IN_FACTION, NO_HOME_SET, SUCCESS, ERROR, HOME_IN_ENEMY_CLAIM
    }
}
