package com.strictmanager.travelbudget.application.member;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class BudgetVO {
    private final Long budgetId;
    private final Long userId;
    private final Long tripPlanId;
    private final Long tripMemberId;
    private final Long amount;

    @Builder
    public BudgetVO(
        @Nullable Long budgetId,
        Long userId,
        Long tripPlanId,
        Long tripMemberId,
        Long amount
    ) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.tripPlanId = requireNonNull(tripPlanId);
        this.tripMemberId = requireNonNull(tripMemberId);
        this.amount = requireNonNull(amount);
    }
}
