package com.strictmanager.travelbudget.web;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.service.PlanService;
import com.strictmanager.travelbudget.domain.user.User;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final BudgetService budgetService;

    @GetMapping("/plans")
    public ResponseEntity<List<TripPlan>> retrievePlans(@AuthenticationPrincipal User user) {
        List<TripPlan> plans = planService.getPlans(user.getId());

        return ResponseEntity.ok(plans);
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

//    @Getter
//    @ToString
//    private class RetrievePlansRequest {
//        private final String ;
//
//    }

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
