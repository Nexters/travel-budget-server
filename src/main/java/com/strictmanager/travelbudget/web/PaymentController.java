package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCaseService paymentCaseService;

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> createBudget(@RequestBody @Valid PaymentCreateRequest request) {
        return null;
    }

    @Getter
    private static class PaymentCreateRequest {

        private final String title;
        private final Long price;
        private final PaymentCaseCategory category;
        private final Long budgetId;

        @JsonCreator
        public PaymentCreateRequest(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "price", required = true) Long price,
            @JsonProperty(value = "category", required = true) PaymentCaseCategory category,
            @JsonProperty(value = "budget_id", required = true) Long budgetId
        ) {
            this.title = title;
            this.price = price;
            this.category = category;
            this.budgetId = budgetId;
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
