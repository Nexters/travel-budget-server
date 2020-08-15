package com.strictmanager.travelbudget.application.plan;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AmountItemVO {
    private final Long purposeAmount;
    private final Double suggestAmount;
    private final Long paymentAmount;
    private final Long budgetId;

    @Builder
    public AmountItemVO(Long purposeAmount, Double suggestAmount, Long paymentAmount, Long budgetId) {
        this.purposeAmount = requireNonNull(purposeAmount);
        this.suggestAmount = requireNonNull(suggestAmount);
        this.paymentAmount = requireNonNull(paymentAmount);
        this.budgetId = requireNonNull(budgetId);
    }
}
