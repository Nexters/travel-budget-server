package com.strictmanager.travelbudget.application.budgetstatistics;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import java.util.EnumMap;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BudgetStatisticsVO {
    private final Long purposeAmount;
    private final Long usedAmount;
    private final EnumMap<PaymentCaseCategory, Long> categories;

    @Builder
    public BudgetStatisticsVO(Long purposeAmount, Long usedAmount, EnumMap<PaymentCaseCategory, Long> categories) {
        this.purposeAmount = requireNonNull(purposeAmount);
        this.usedAmount = requireNonNull(usedAmount);
        this.categories = requireNonNull(categories);
    }
}
