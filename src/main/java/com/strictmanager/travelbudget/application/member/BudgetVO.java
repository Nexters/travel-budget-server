package com.strictmanager.travelbudget.application.member;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BudgetVO {
    private final Long userId;
    private final Long tripPlanId;
    private final Long tripMemberId;
    private final Long amount;

    @Builder
    public BudgetVO(
        Long userId,
        Long tripPlanId,
        Long tripMemberId,
        Long amount
    ) {
        this.userId = userId;
        this.tripPlanId = requireNonNull(tripPlanId);
        this.tripMemberId = requireNonNull(tripMemberId);
        this.amount = requireNonNull(amount);
    }
}
