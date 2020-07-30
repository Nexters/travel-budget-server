package com.strictmanager.travelbudget.web;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.domain.plan.service.PlanService;
import com.strictmanager.travelbudget.domain.user.User;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final BudgetService budgetService;

    @GetMapping("/plans")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PlanResponse>> retrievePlans(
        @AuthenticationPrincipal User user,
        @RequestParam(name = "isComing") boolean isComing) {

        Stream<TripPlan> plans;
        if (isComing) {
            List<TripPlan> doingPlans = planService.getDoingPlans(user.getId());
            doingPlans.addAll(planService.getComingPlans(user.getId()));

            plans = doingPlans.stream();
        } else {
            plans = planService.getFinishPlans(user.getId());
        }

        List<PlanResponse> plansResponse = plans
            .map(plan -> PlanResponse.builder()
                .name(plan.getName())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .amount(
                    Objects.requireNonNullElseGet(plan.getBudget(),
                        () -> Budget.builder().amount(-1L).build())
                        .getAmount())
                .isPublic(plan.getIsPublic())
                .userCount(
                    plan.getTripMembers().size())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(plansResponse);
    }

    @PostMapping("/plans")
    public ResponseEntity createPlan(@AuthenticationPrincipal User user,
        HttpServletRequest httpServletRequest,
        @RequestBody CreatePlanRequest param) {

        planService.checkDateValidation(param.getStartDate(), param.getEndDate());

        Optional<Long> sharedBudgetOpt = Optional.ofNullable(param.getSharedBudget());

        Budget budget = sharedBudgetOpt
            .map(amount -> budgetService.createBudget(Budget.builder()
                .amount(amount)
                .build()))
            .orElse(null);

        TripPlan tripPlan = planService.createPlan(TripPlan.builder()
            .name(param.name)
            .startDate(param.getStartDate())
            .endDate(param.getEndDate())
            .budget(budget)
            .userId(user.getId())
            .build());

        planService.createTripMember(TripMember.builder()
            .authority(Authority.OWNER)
            .tripPlan(tripPlan)
            .budget(budget)
            .user(user)
            .build());

        return ResponseEntity
            .created(URI.create(httpServletRequest.getRequestURI()))
            .build();
    }

    @Getter
    @ToString
    private static class PlanResponse {

        private final String name;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Long amount; // null이면 미지정 text 출력
        private final int userCount;

        @Builder
        @JsonCreator
        public PlanResponse(
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "start_date") LocalDate startDate,
            @JsonProperty(value = "end_date") LocalDate endDate,
            @JsonProperty(value = "amount") Long amount,
            @JsonProperty(value = "user_count") int userCount,
            @JsonProperty(value = "is_public") YnFlag isPublic
        ) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.amount = amount;
            this.userCount = userCount;
            this.isPublic = isPublic;
        }

        private final YnFlag isPublic;
    }

    @Getter
    @ToString
    public static class CreatePlanRequest {

        private final String name;
        private final LocalDate startDate;
        private final LocalDate endDate;

        private final Long sharedBudget;

        @JsonCreator
        public CreatePlanRequest(
            @JsonProperty(value = "name", defaultValue = "여행을 떠나요") String name,
            @JsonProperty(value = "start_date", required = true) LocalDate startDate,
            @JsonProperty(value = "end_date", required = true) LocalDate endDate,
            @JsonProperty(value = "shared_budget", required = false) Long sharedBudget
        ) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.sharedBudget = sharedBudget;
        }
    }

}
