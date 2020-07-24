package com.strictmanager.travelbudget.web;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.service.PlanService;
import com.strictmanager.travelbudget.domain.user.User;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
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

    @GetMapping("/plans")
    public ResponseEntity<List<TripPlan>> getUserPlans(@AuthenticationPrincipal User user) {
        List<TripPlan> plans = planService.getPlans(user.getId());
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/plans") // TODO: plan? plans 2020-07-24 (kiyeon_kim1)
    public ResponseEntity<Object> createPlan(@AuthenticationPrincipal User user,
        @RequestBody @Valid PlanCreateRequest planCreateRequest) {

        // TODO: controller 연결 필요 2020-07-24 (kiyeon_kim1)

        return ResponseEntity.ok(null);
//        return ResponseEntity.created(linkTo(PlanController.class).toUri()).build();
    }

    @Getter
    @ToString
    private static class PlanCreateRequest {

        private final String name;
        private final LocalDate startDate;
        private final LocalDate endDate;

        private final Long sharedBudget;
        private final Long personalBudget;

        @JsonCreator
        public PlanCreateRequest(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "startDate", required = true) LocalDate startDate,
            @JsonProperty(value = "endDate", required = true) LocalDate endDate,
            @JsonProperty(value = "sharedBudget", required = false) Long sharedBudget,
            @JsonProperty(value = "personalBudget", required = true) Long personalBudget
        ) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.sharedBudget = Objects.requireNonNullElse(sharedBudget, 0L);
            this.personalBudget = personalBudget;
        }
    }

}
