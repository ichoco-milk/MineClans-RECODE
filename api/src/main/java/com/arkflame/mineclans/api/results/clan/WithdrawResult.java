package com.arkflame.mineclans.api.results.clan;

public record WithdrawResult(
    WithdrawResultType resultType,
    double amountWithdrawn
) {
    public enum WithdrawResultType {
        SUCCESS,
        NOT_IN_FACTION,
        NO_PERMISSION,
        INSUFFICIENT_FUNDS,
        ERROR,
        INVALID_AMOUNT,
        NO_VAULT, NO_ECONOMY
    }
}
