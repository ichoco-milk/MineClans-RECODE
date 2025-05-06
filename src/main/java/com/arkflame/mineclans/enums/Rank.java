package com.arkflame.mineclans.enums;

public enum Rank {
    LEADER(5),
    COLEADER(4),
    MODERATOR(3),
    MEMBER(2),
    RECRUIT(1);

    private final int score;

    Rank(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public Rank getNext() {
        switch (this) {
            case RECRUIT:
                return MEMBER;
            case MEMBER:
                return MODERATOR;
            case MODERATOR:
                return COLEADER;
            case COLEADER:
                return LEADER;
            default:
                return null; // LEADER has no next rank
        }
    }

    public Rank getPrevious() {
        switch (this) {
            case LEADER:
                return COLEADER;
            case COLEADER:
                return MODERATOR;
            case MODERATOR:
                return MEMBER;
            case MEMBER:
                return RECRUIT;
            default:
                return null; // RECRUIT has no previous rank
        }
    }

    public boolean isEqualOrHigherThan(Rank rank) {
        return this.score >= rank.score;
    }

    public boolean isLowerThan(Rank rank) {
        return this.score < rank.score;
    }
}
