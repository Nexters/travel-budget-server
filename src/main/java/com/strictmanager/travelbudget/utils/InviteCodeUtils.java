package com.strictmanager.travelbudget.utils;

import java.util.Optional;

public final class InviteCodeUtils {

    private InviteCodeUtils() { }

    public static String generatePlanInviteCode(Long planId, Long userId) {
        return String.format("p%du%d", planId, userId);
    }

    public static Optional<Long> getPlanIdFromInviteCode(String inviteCode) {
        if (isNotValidCode(inviteCode)) {
            return Optional.empty();
        }

        String[] arr = getCodeArray(inviteCode);

        if (isNotValidArrayLength(arr)) {
            return Optional.empty();
        }

        return Optional.of(Long.valueOf(arr[0]));
    }

    public static Optional<Long> getPlanInviteUserId(String inviteCode) {
        if (isNotValidCode(inviteCode)) {
            return Optional.empty();
        }

        String[] arr = getCodeArray(inviteCode);

        if (isNotValidArrayLength(arr)) {
            return Optional.empty();
        }

        return Optional.of(Long.valueOf(arr[1]));
    }


    private static boolean isNotValidCode(String inviteCode) {
        return !inviteCode.startsWith("p") || !inviteCode.contains("u");
    }

    private static String[] getCodeArray(String inviteCode) {
        final String str = inviteCode.substring(1);
        final String[] arr = str.split("u");

        return arr;
    }

    private static boolean isNotValidArrayLength(String[] arr) {
        return arr.length < 2;
    }

}
