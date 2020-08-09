package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanBudgetController {

    private final BudgetService budgetService;

    @PutMapping("/budgets/{id}")
    @ApiOperation(value = "목표 예산 변경 (여행 & 개인 전체)")
    public ResponseEntity<BudgetResponse> updateBudget(
        @AuthenticationPrincipal User user,
        @PathVariable(name = "id") Long budgetId,
        @RequestBody @Valid BudgetUpdateRequest request
    ) {

        final Budget budget = budgetService
            .updateBudgetAmount(user.getId(), budgetId, request.getAmount());

        return ResponseEntity.ok(new BudgetResponse(budget.getId()));
    }

    @Getter
    @ApiModel
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class BudgetUpdateRequest {

        @ApiModelProperty(name = "목표 예산")
        private final Long amount;

        @JsonCreator
        private BudgetUpdateRequest(
            @JsonProperty(value = "amount", required = true) Long amount
        ) {
            this.amount = amount;
        }
    }

    @Getter
    private static class BudgetResponse {

        private final Long budgetId;

        private BudgetResponse(Long budgetId) {
            this.budgetId = requireNonNull(budgetId);
        }
    }
}
