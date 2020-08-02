package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.strictmanager.travelbudget.application.member.BudgetVO;
import com.strictmanager.travelbudget.application.member.MemberBudgetManager;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.user.User;
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
public class BudgetController {

    private final MemberBudgetManager memberBudgetManager;
    private final BudgetService budgetService;

    @PostMapping("/budgets")
    public ResponseEntity<BudgetResponse> createBudget(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid BudgetCreateRequest request
    ) {
        final Long budgetId = memberBudgetManager.createMemberBudget(
            BudgetVO.builder()
                .userId(user.getId())
                .tripPlanId(request.getTripPlanId())
                .tripMemberId(request.getTripMemberId())
                .amount(request.getAmount())
                .build()
        );

        return ResponseEntity.ok(new BudgetResponse(budgetId));
    }

    @PutMapping("/budgets/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
        @AuthenticationPrincipal User user,
        @PathVariable @Valid Long id,
        @RequestBody @Valid BudgetUpdateRequest request
    ) {
        final Budget budget = budgetService.updateBudgetAmount(user.getId(), id, request.getAmount());

        return ResponseEntity.ok(new BudgetResponse(budget.getId()));
    }

    @Getter
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class BudgetCreateRequest {

        private final Long tripPlanId;
        private final Long tripMemberId;
        private final Long amount;

        @JsonCreator
        private BudgetCreateRequest(
            @JsonProperty(value = "trip_plan_id", required = true) Long tripPlanId,
            @JsonProperty(value = "trip_member_id", required = true) Long tripMemberId,
            @JsonProperty(value = "amount", required = true) Long amount
        ) {
            this.tripPlanId = tripPlanId;
            this.tripMemberId = tripMemberId;
            this.amount = amount;
        }
    }

    @Getter
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class BudgetUpdateRequest {

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
