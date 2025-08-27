package com.arkflame.mineclans.api.results.chat;

public record ToggleChatResult(
    ToggleChatState state
) {
    public enum ToggleChatState {
        FACTION,
        ALLIANCE,
        DISABLED,
        NOT_IN_FACTION;

        public ToggleChatState getNext() {
            return switch (this) {
                case FACTION -> ALLIANCE;
                case ALLIANCE -> DISABLED;
                case DISABLED -> FACTION;
                default -> NOT_IN_FACTION;
            };
        }
    }
}
