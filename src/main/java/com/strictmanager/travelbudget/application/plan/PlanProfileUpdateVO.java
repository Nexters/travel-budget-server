package com.strictmanager.travelbudget.application.plan;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanProfileUpdateVO {

    private final User user;
    private final Long planId;
    private final String name;
    private final Long publicAmount;
    private final Long personalAmount;

    @Builder
    PlanProfileUpdateVO(
        User user,
        Long planId,
        String name,
        Long publicAmount,
        Long personalAmount) {
        this.user = requireNonNull(user);
        this.planId = requireNonNull(planId);
        this.name = requireNonNull(name);
        this.publicAmount = publicAmount;
        this.personalAmount = requireNonNull(personalAmount);
    }
}
