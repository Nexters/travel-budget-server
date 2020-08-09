package com.strictmanager.travelbudget.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InviteCodeUtilsTest {
    @Test
    void generatePlanInviteCode() {
        String inviteCode = InviteCodeUtils.generatePlanInviteCode(123L, 10L);
        // System.out.println(inviteCode);
        Assertions.assertNotNull(inviteCode);
    }

    @Test
    void getPlanIdFromInviteCode() {
        String inviteCode = "p12u1";
        Assertions.assertEquals(12L, InviteCodeUtils.getPlanIdFromInviteCode(inviteCode).get());

        inviteCode = "p123u12";
        Assertions.assertEquals(123L, InviteCodeUtils.getPlanIdFromInviteCode(inviteCode).get());
    }

    @Test
    void getPlanIdFromInviteCode_fail_when_invalid_code() {
        String inviteCode = "1u1";
        Assertions.assertNull(InviteCodeUtils.getPlanIdFromInviteCode(inviteCode).orElse(null));

        inviteCode = "p11";
        Assertions.assertNull(InviteCodeUtils.getPlanIdFromInviteCode(inviteCode).orElse(null));
    }
}
