package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentVO {

    private final Long id;
    private final Long price;
    private final String title;
    private final PaymentCaseCategory category;


    @Builder
    public PaymentVO(
        Long id,
        Long price,
        String title,
        PaymentCaseCategory category) {
        this.id = Objects.requireNonNull(id);
        this.price = Objects.requireNonNull(price);
        this.title = Objects.requireNonNullElse(title, "미입력 제목");
        this.category = category;
    }
}
