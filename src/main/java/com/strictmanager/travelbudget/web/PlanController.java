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
//@DevController // TODO: 개발 단계에서 사용 2020-07-25 (kiyeon_kim1)
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final static Long DEV_USER_ID = 1L; // TODO: 개발단계에서 사 2020-07-25 (kiyeon_kim1)

    @GetMapping("/plans")
    public ResponseEntity<List<TripPlan>> getUserPlans(@AuthenticationPrincipal User user) {
        List<TripPlan> plans = planService.getPlans(user.getId());
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/plans")
    public ResponseEntity createPlan(@AuthenticationPrincipal User user,
        HttpServletRequest httpServletRequest,
        @RequestBody PlanCreateRequest planCreateRequest) {

        return ResponseEntity
            .created(URI.create(httpServletRequest.getRequestURI()))
            .build();
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
