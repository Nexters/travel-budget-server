package com.strictmanager.travelbudget.application.plan;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.YnFlag;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanVO {
    private final Long planId;
    private final Long budgetId;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Long purposeAmount;
    private final Long usedAmount;
    private final int userCount;
    private final YnFlag isPublic;
    private final YnFlag isDoing;
    private final String inviteCode;

    @Builder
    public PlanVO(
        Long planId,
        Long budgetId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Long purposeAmount,
        Long usedAmount,
        int userCount,
        YnFlag isPublic,
        YnFlag isDoing,
        String inviteCode
    ) {
        this.planId = requireNonNull(planId);
        this.budgetId = requireNonNull(budgetId);
        this.name = requireNonNull(name);
        this.startDate = requireNonNull(startDate);
        this.endDate = requireNonNull(endDate);
        this.purposeAmount = requireNonNull(purposeAmount);
        this.usedAmount = requireNonNull(usedAmount);
        this.userCount = requireNonNull(userCount);
        this.isPublic = requireNonNull(isPublic);
        this.isDoing = requireNonNull(isDoing);
        this.inviteCode = requireNonNull(inviteCode);
    }
}
