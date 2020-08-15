package com.strictmanager.travelbudget.application.plan;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanCreateVO {

    private final User createUser;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Long sharedBudget;
    private final YnFlag isPublic;

    @Builder
    public PlanCreateVO(
        User user,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Long sharedBudget,
        YnFlag isPublic) {
        this.createUser = requireNonNull(user);
        this.name = requireNonNull(name);
        this.startDate = requireNonNull(startDate);
        this.endDate = requireNonNull(endDate);
        this.sharedBudget = requireNonNull(sharedBudget);
        this.isPublic = requireNonNull(isPublic);
    }
}
