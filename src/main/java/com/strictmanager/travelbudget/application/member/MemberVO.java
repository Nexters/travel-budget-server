package com.strictmanager.travelbudget.application.member;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberVO {

    private final User user;
    private final Long planId;
    private final Long memberId;


    @Builder
    public MemberVO(User user, Long planId, Long memberId) {
        this.user = requireNonNull(user);
        this.planId = requireNonNull(planId);
        this.memberId = requireNonNull(memberId);
    }
}
