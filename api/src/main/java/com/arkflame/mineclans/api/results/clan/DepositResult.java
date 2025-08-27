package com.arkflame.mineclans.api.results.clan;

public record DepositResult(
    DepositResultType resultType,
    double amountDeposited
) {
    public enum DepositResultType {
        SUCCESS,
        NOT_IN_FACTION,
        NO_PERMISSION,
        ERROR, 
        INVALID_AMOUNT,
        NO_VAULT, NO_ECONOMY, NO_MONEY
    }
}
