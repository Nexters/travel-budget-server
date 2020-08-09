package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.application.member.BudgetVO;
import com.strictmanager.travelbudget.application.member.BudgetManager;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanMemberController {

    private final BudgetManager budgetManager;

    @PostMapping("/members/{id}/budgets")
    @ApiOperation(value = "개인 목표 예산 설정")
    public ResponseEntity<BudgetResponse> createBudget(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid BudgetCreateRequest request,
        @PathVariable(name = "id") Long memberId) {

        final Long budgetId = budgetManager.createMemberBudget(
            BudgetVO.builder()
                .userId(user.getId())
                .memberId(memberId)
                .amount(request.getAmount())
                .build()
        );

        return ResponseEntity.ok(new BudgetResponse(budgetId));
    }

    @Getter
    @ApiModel
    private static class BudgetCreateRequest {

        @ApiModelProperty(name = "목표 예산")
        private final Long amount;

        @JsonCreator
        private BudgetCreateRequest(
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
