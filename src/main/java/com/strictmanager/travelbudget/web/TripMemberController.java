package com.strictmanager.travelbudget.web;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.strictmanager.travelbudget.application.member.BudgetVO;
import com.strictmanager.travelbudget.application.member.MemberBudgetManager;
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
public class TripMemberController {

    private final MemberBudgetManager memberBudgetManager;

    @PostMapping("/members/{id}/budgets")
    @ApiOperation(value = "개인 목표 예산 설정")
    public ResponseEntity<BudgetResponse> createBudget(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid BudgetCreateRequest request,
        @PathVariable(name = "id") Long planId) {

        final Long budgetId = memberBudgetManager.createMemberBudget(
            BudgetVO.builder()
                .user(user)
                .planId(planId)
                .amount(request.getAmount())
                .build()
        );

        return ResponseEntity.ok(new BudgetResponse(budgetId));
    }


    @Getter
    @ApiModel
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
