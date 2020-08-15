package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.application.budgetstatistics.BudgetStatisticsManager;
import com.strictmanager.travelbudget.application.budgetstatistics.BudgetStatisticsVO;
import com.strictmanager.travelbudget.application.member.BudgetManager;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import com.strictmanager.travelbudget.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.util.EnumMap;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanBudgetController {

    private final BudgetManager budgetManager;
    private final BudgetStatisticsManager budgetStatisticsManager;

    @PutMapping("/budgets/{id}")
    @ApiOperation(value = "목표 예산 변경 (여행 & 개인 전체)")
    public ResponseEntity<BudgetResponse> updateBudget(
        @AuthenticationPrincipal User user,
        @PathVariable(name = "id") Long budgetId,
        @RequestBody @Valid BudgetUpdateRequest request
    ) {

        budgetManager.updateBudgetAmount(user.getId(), budgetId, request.getAmount());
        final Budget budget = budgetManager
            .updateBudgetAmount(user.getId(), budgetId, request.getAmount());

        return ResponseEntity.ok(new BudgetResponse(budget.getId()));
    }

    @GetMapping("/budgets/{id}/statics")
    @ApiOperation(value = "지출 통계 조회")
    @Transactional(readOnly = true)
    public ResponseEntity<BudgetStatisticsResponse> getBudgetStatics(
        @PathVariable(name = "id") Long budgetId
    ) {
        BudgetStatisticsVO budgetStatisticsVO = budgetStatisticsManager.getStatics(budgetId);

        return ResponseEntity.ok(
            BudgetStatisticsResponse
                .builder()
                .purposeAmount(budgetStatisticsVO.getPurposeAmount())
                .usedAmount(budgetStatisticsVO.getUsedAmount())
                .categories(budgetStatisticsVO.getCategories())
                .build()
        );
    }

    @Getter
    @ApiModel
    private static class BudgetStatisticsResponse {

        @ApiModelProperty(name = "목표 예산")
        private final Long purposeAmount;

        @ApiModelProperty(name = "사용한 예산")
        private final Long usedAmount;

        @ApiModelProperty(name = "카테고리별 사용 예산")
        private final EnumMap<PaymentCaseCategory, Long> categories;

        @Builder
        BudgetStatisticsResponse(Long purposeAmount, Long usedAmount,
            EnumMap<PaymentCaseCategory, Long> categories) {
            this.purposeAmount = purposeAmount;
            this.usedAmount = usedAmount;
            this.categories = categories;
        }
    }

    @Getter
    @ApiModel
    private static class BudgetUpdateRequest {

        @ApiModelProperty(name = "목표 예산")
        private final Long amount;

        @JsonCreator
        BudgetUpdateRequest(
            @JsonProperty(value = "amount", required = true) Long amount
        ) {
            this.amount = amount;
        }
    }

    @Getter
    private static class BudgetResponse {

        private final Long budgetId;

        BudgetResponse(Long budgetId) {
            this.budgetId = requireNonNull(budgetId);
        }
    }
}
