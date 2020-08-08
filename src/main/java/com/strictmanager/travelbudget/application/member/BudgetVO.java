package com.strictmanager.travelbudget.application.member;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BudgetVO {

    private final Long userId;
    private final Long planId;
    private final Long amount;
    private final Long memberId;

    @Builder
    public BudgetVO(
        Long userId,
        Long planId,
        Long amount,
        Long memberId) {
        this.userId = requireNonNull(userId);
        this.planId = requireNonNull(planId);
        this.amount = requireNonNull(amount);
        this.memberId = requireNonNull(memberId);
    }
}
