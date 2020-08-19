package com.strictmanager.travelbudget.application.plan;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanProfileVO {

    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final AmountVO shared;
    private final AmountVO personal;
    private final Authority authority;


    @Builder
    public PlanProfileVO(String name, LocalDate startDate, LocalDate endDate,
        AmountVO shared, AmountVO personal,
        Authority authority) {
        this.name = requireNonNull(name);
        this.startDate = requireNonNull(startDate);
        this.endDate = requireNonNull(endDate);
        this.shared = shared;
        this.personal = personal;
        this.authority = requireNonNull(authority);
    }

    @Getter
    public static class AmountVO {

        private final Long budgetId;
        private final Long amount;

        @Builder
        public AmountVO(Long budgetId, Long amount) {
            this.budgetId = budgetId;
            this.amount = amount;
        }
    }
}
