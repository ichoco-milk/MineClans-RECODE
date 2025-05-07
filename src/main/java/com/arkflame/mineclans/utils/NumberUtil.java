package com.arkflame.mineclans.utils;

import java.text.DecimalFormat;

public class NumberUtil {
    private static final DecimalFormat FORMAT = new DecimalFormat("#.#");
    
    public static String formatBalance(double balance) {
        if (balance >= 1e9) {
            return FORMAT.format(balance / 1e9) + "B";
        } else if (balance >= 1e6) {
            return FORMAT.format(balance / 1e6) + "M";
        } else if (balance >= 1e3) {
            return FORMAT.format(balance / 1e3) + "K";
        } else {
            return FORMAT.format(balance);
        }
    }

    public static String formatPower(double power) {
        return String.valueOf(Math.round(power * 10) / 10);
    }

    public static String formatScore(double score) {
        return String.valueOf(Math.round(score * 10) / 10);
    }
}
