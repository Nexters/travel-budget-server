package com.strictmanager.travelbudget.application.member;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BudgetVO {

    private final User user;
    private final Long planId;
    private final Long amount;
    private final Long memberId;

    @Builder
    public BudgetVO(
        User user,
        Long planId,
        Long amount,
        Long memberId) {
        this.user = requireNonNull(user);
        this.planId = requireNonNull(planId);
        this.amount = requireNonNull(amount);
        this.memberId = requireNonNull(memberId);
    }
}
