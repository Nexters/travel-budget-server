package com.strictmanager.travelbudget.utils;

import java.util.Optional;

public final class InviteCodeUtils {
    private InviteCodeUtils() {}

    public static String generatePlanInviteCode(Long planId, Long userId) {
        return String.format("p%du%d", planId, userId);
    }

    public static Optional<Long> getPlanIdFromInviteCode(String inviteCode) {
        if (!inviteCode.startsWith("p") || !inviteCode.contains("u")) {
            return Optional.empty();
        }
        final String str = inviteCode.substring(1);
        final String[] arr = str.split("u");
        if (arr.length < 2) {
            return Optional.empty();
        }

        return Optional.of(Long.valueOf(arr[0]));
    }
}
