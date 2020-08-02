package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.application.payment.PaymentManager;
import com.strictmanager.travelbudget.application.payment.PaymentVO;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentManager paymentManager;

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> createPayment(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid PaymentRequest request
    ) {
        final Long paymentCaseId = paymentManager.createPaymentCase(
            PaymentVO.builder()
                .userId(user.getId())
                .budgetId(request.getBudgetId())
                .title(request.getTitle())
                .price(request.getPrice())
                .paymentCaseCategory(request.getCategory())
                .paymentDt(request.getPaymentDt())
                .build()
        );

        return ResponseEntity.ok(new PaymentResponse(paymentCaseId));
    }

    @PutMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> updatePayment(
        @AuthenticationPrincipal User user,
        @PathVariable @Valid Long paymentId,
        @RequestBody @Valid PaymentRequest request
    ) {
        final Long paymentCaseId = paymentManager.updatePaymentCase(
            user.getId(),
            paymentId,
            PaymentVO.builder()
                .userId(user.getId())
                .budgetId(request.getBudgetId())
                .title(request.getTitle())
                .price(request.getPrice())
                .paymentCaseCategory(request.getCategory())
                .paymentDt(request.getPaymentDt())
                .build()
        );

        return ResponseEntity.ok(new PaymentResponse(paymentCaseId));
    }

    @Getter
    private static class PaymentRequest {

        private final String title;
        private final Long price;
        private final PaymentCaseCategory category;
        private final Long budgetId;
        private final Long paymentTs;

        @JsonCreator
        private PaymentRequest(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "price", required = true) Long price,
            @JsonProperty(value = "category", required = true) PaymentCaseCategory category,
            @JsonProperty(value = "budget_id", required = true) Long budgetId,
            @JsonProperty(value = "payment_ts", required = true) Long paymentTs
        ) {
            this.title = title;
            this.price = price;
            this.category = category;
            this.budgetId = budgetId;
            this.paymentTs = paymentTs;
        }

        private LocalDateTime getPaymentDt() {
            return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(paymentTs),
                TimeZone.getDefault().toZoneId()
            );
        }
    }

    @Getter
    private static class PaymentResponse {

        private final Long paymentId;

        private PaymentResponse(Long paymentId) {
            this.paymentId = requireNonNull(paymentId);
        }
    }
}
