package com.strictmanager.travelbudget.application.payment;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class PaymentVO {
    private final Long userId;
    private final Long budgetId;
    private final String title;
    private final Long price;
    private final PaymentCaseCategory paymentCaseCategory;
    private final LocalDateTime paymentDt;
    private final YnFlag isReady;

    @Builder
    public PaymentVO(
        Long userId,
        Long budgetId,
        String title,
        Long price,
        PaymentCaseCategory paymentCaseCategory,
        LocalDateTime paymentDt,
        YnFlag isReady
    ) {
        this.userId = requireNonNull(userId);
        this.budgetId = requireNonNull(budgetId);
        this.title = requireNonNull(title);
        this.price = requireNonNull(price);
        this.paymentCaseCategory = requireNonNull(paymentCaseCategory);
        this.paymentDt = requireNonNull(paymentDt);
        this.isReady = requireNonNull(isReady);
    }
}
