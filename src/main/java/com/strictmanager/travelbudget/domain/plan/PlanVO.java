package com.strictmanager.travelbudget.domain.plan;

import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanVO {

    private final User createUser;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Long sharedBudget;

    @Builder
    public PlanVO(
        User user,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Long sharedBudget
    ) {
        this.createUser = user;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sharedBudget = sharedBudget;
    }

}
