package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.application.payment.PaymentManager;
import com.strictmanager.travelbudget.application.payment.PaymentVO;
import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.utils.LocalDateTimeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @ApiOperation(value = "지출내역 조회")
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getPayments(
        @AuthenticationPrincipal User user,
        @ApiParam(value = "예산 ID", required = true) @RequestParam(name = "budget_id") Long budgetId,
        @ApiParam(value = "사전 지출 여부", required = false, example = "Y") @RequestParam(name = "is_ready", required = false, defaultValue = "N") YnFlag isReady,
        @ApiParam(value = "조회할 날짜", required = false, example = "2020-08-01") @RequestParam(name = "payment_dt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate paymentDt
    ) {
        final List<PaymentCase> paymentCases = paymentManager
            .getPaymentCases(user.getId(), budgetId, isReady, paymentDt);

        return ResponseEntity.ok(
            paymentCases.stream()
                .map(paymentCase ->
                    new PaymentResponse(
                        paymentCase.getId(),
                        paymentCase.getPrice(),
                        paymentCase.getTitle(),
                        paymentCase.getPaymentDt(),
                        paymentCase.getCategory(),
                        paymentCase.getBudget().getId(),
                        paymentCase.getIsReady()))
                .collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "지출내역 기록")
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
                .isReady(request.getIsReady())
                .build()
        );

        return ResponseEntity.ok(new CreatePaymentResponse(paymentCaseId));
    }

    @ApiOperation(value = "지출내역 수정")
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
                .isReady(request.getIsReady())
                .build()
        );

        return ResponseEntity.ok(new CreatePaymentResponse(paymentCaseId));
    }

    @ApiOperation(value = "지출내역 삭제")
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<?> deletePayment(
        @PathVariable @Valid Long paymentId
    ) {
        paymentManager.deletePaymentCase(paymentId);

        return ResponseEntity.noContent().build();
    }

    @ApiModel
    @Getter
    private static class PaymentRequest {

        private final String title;
        private final Long price;
        @ApiModelProperty(value = "카테고리", required = true, example = "TRAFFIC")
        private final PaymentCaseCategory category;
        private final Long budgetId;
        @ApiModelProperty(value = "지출 시간", dataType = "Long", required = true, example = "1596867429")
        private final LocalDateTime paymentDt;
        @ApiModelProperty(value = "사전 기록", required = true, example = "N")
        private final YnFlag isReady;

        @JsonCreator
        private PaymentRequest(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "price", required = true) Long price,
            @JsonProperty(value = "category", required = true) PaymentCaseCategory category,
            @JsonProperty(value = "budget_id", required = true) Long budgetId,
            @JsonProperty(value = "payment_dt", required = true) Long paymentDt,
            @JsonProperty(value = "is_ready", required = true) YnFlag isReady
        ) {
            this.title = title;
            this.price = price;
            this.category = category;
            this.budgetId = budgetId;
            this.paymentDt = LocalDateTimeUtils.convertToLocalDateTime(paymentDt);
            this.isReady = isReady;
        }
    }

    @Getter
    private static class CreatePaymentResponse {

        private final Long paymentId;

        CreatePaymentResponse(Long paymentId) {
            this.paymentId = requireNonNull(paymentId);
        }
    }

    @Getter
    private static class PaymentResponse {

        private final Long paymentId;
        private final Long price;
        private final String title;
        private final Long paymentDt;
        private final PaymentCaseCategory category;
        private final Long budgetId;
        private final YnFlag isReady;

        PaymentResponse(
            Long paymentId,
            Long price,
            String title,
            LocalDateTime paymentDt,
            PaymentCaseCategory category,
            Long budgetId,
            YnFlag isReady
        ) {
            this.paymentId = paymentId;
            this.price = price;
            this.title = title;
            this.paymentDt = LocalDateTimeUtils.convertToTimestamp(paymentDt);
            this.category = category;
            this.budgetId = budgetId;
            this.isReady = isReady;
        }
    }
}
