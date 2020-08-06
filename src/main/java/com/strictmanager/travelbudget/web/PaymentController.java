package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.application.payment.PaymentManager;
import com.strictmanager.travelbudget.application.payment.PaymentVO;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.utils.LocalDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentManager paymentManager;

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getPayments(
        @AuthenticationPrincipal User user,
        @RequestParam(name = "budget_id") Long budgetId,
        @RequestParam(name = "payment_dt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate paymentDt
    ) {
        final List<PaymentCase> paymentCases = paymentManager.getPaymentCases(user.getId(), budgetId, paymentDt);

        return ResponseEntity.ok(
            paymentCases.stream()
                .map(paymentCase ->
                    new PaymentResponse(
                        paymentCase.getPrice(),
                        paymentCase.getTitle(),
                        paymentCase.getPaymentDt(),
                        paymentCase.getCategory(),
                        paymentCase.getBudget().getId()))
                .collect(Collectors.toList())
        );
    }

    @PostMapping("/payments")
    public ResponseEntity<CreatePaymentResponse> createPayment(
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

        return ResponseEntity.ok(new CreatePaymentResponse(paymentCaseId));
    }

    @PutMapping("/payments/{paymentId}")
    public ResponseEntity<CreatePaymentResponse> updatePayment(
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

        return ResponseEntity.ok(new CreatePaymentResponse(paymentCaseId));
    }

    @Getter
    private static class PaymentRequest {

        private final String title;
        private final Long price;
        private final PaymentCaseCategory category;
        private final Long budgetId;
        private final LocalDateTime paymentDt;

        @JsonCreator
        private PaymentRequest(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "price", required = true) Long price,
            @JsonProperty(value = "category", required = true) PaymentCaseCategory category,
            @JsonProperty(value = "budget_id", required = true) Long budgetId,
            @JsonProperty(value = "payment_dt", required = true) Long paymentDt
        ) {
            this.title = title;
            this.price = price;
            this.category = category;
            this.budgetId = budgetId;
            this.paymentDt = LocalDateTimeUtils.convertToLocalDateTime(paymentDt);
        }
    }

    @Getter
    private static class CreatePaymentResponse {

        private final Long paymentId;

        private CreatePaymentResponse(Long paymentId) {
            this.paymentId = requireNonNull(paymentId);
        }
    }

    @Getter
    private static class PaymentResponse {

        private final Long price;
        private final String title;
        private final Long paymentDt;
        private final PaymentCaseCategory category;
        private final Long budgetId;

        public PaymentResponse(
            Long price,
            String title,
            LocalDateTime paymentDt,
            PaymentCaseCategory category,
            Long budgetId
        ) {
            this.price = price;
            this.title = title;
            this.paymentDt = LocalDateTimeUtils.convertToTimestamp(paymentDt);
            this.category = category;
            this.budgetId = budgetId;
        }
    }
}
