package com.strictmanager.travelbudget.web;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.strictmanager.travelbudget.application.member.MemberBudgetManager;
import com.strictmanager.travelbudget.application.member.PlanManager;
import com.strictmanager.travelbudget.application.member.PlanVO;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.domain.user.User;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@ApiController
@RequiredArgsConstructor
public class PlanController {


    private final PlanManager planManager;
    private final MemberBudgetManager memberBudgetManager;

    @GetMapping("/plans")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PlanResponse>> getPlans(
        @AuthenticationPrincipal User user,
        @RequestParam(name = "isComing") boolean isComing) {
        List<PlanResponse> responses = planManager.getPlans(user, isComing);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/plans")
    public ResponseEntity createPlan(@AuthenticationPrincipal @Valid User user,
        HttpServletRequest httpServletRequest,
        @RequestBody CreatePlanRequest param) {

        planManager.createPlan(PlanVO.builder()
            .user(user)
            .name(param.getName())
            .startDate(param.getStartDate())
            .endDate(param.getEndDate())
            .sharedBudget(param.getSharedBudget())
            .build());

        return ResponseEntity
            .created(URI.create(httpServletRequest.getRequestURI()))
            .build();
    }


    @GetMapping("/plans/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<PlanDetailResponse> getPlanDetail(@AuthenticationPrincipal User user,
        @PathVariable(value = "id") Long planId) {

        PlanDetailResponse planDetailResponse = planManager.getPlanDetail(user, planId);

        return ResponseEntity.ok(planDetailResponse);
    }


    @Getter
    public static class PlanDetailResponse {

        private final Long purposeAmount; //전체예산
        private final Double suggestAmount; // 일자별 제안 예
        private final Long totalUseAmount; // 사용된 예산

        private final List<LocalDate> dates;

        @Builder
        public PlanDetailResponse(Long purposeAmount, Double suggestAmount, Long totalUseAmount,
            List<LocalDate> dates) {
            this.purposeAmount = purposeAmount;
            this.suggestAmount = suggestAmount;
            this.totalUseAmount = totalUseAmount;
            this.dates = dates;
        }
    }


    @Getter
    public static class PlanResponse {

        private final Long id;
        private final String name;
        private final String planPeriod;
        private final String dayCount;
        private final Long amount; // 금액 미입력시 -1
        private final int userCount;
        private final YnFlag isPublic;

        @Builder
        public PlanResponse(
            Long id,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Long amount,
            int userCount,
            YnFlag isPublic
        ) {
            this.id = id;
            this.name = name;
            this.planPeriod = convertPlanPeriod(startDate, endDate);
            this.dayCount = calculateDay(startDate);
            this.amount = amount;
            this.userCount = userCount;
            this.isPublic = isPublic;
        }

        private String calculateDay(LocalDate startDate) {
            StringBuilder dDayBuilder = new StringBuilder("D");

            ChronoUnit chronoUnit = ChronoUnit.DAYS;

            long day = chronoUnit.between(startDate, LocalDate.now());

            if (day > 0) {
                dDayBuilder.append("+").append(day);
            } else if (day == 0) {
                dDayBuilder.append("-DAY!");
            } else {
                dDayBuilder.append(day);
            }
            return dDayBuilder.toString();
        }


        private String convertPlanPeriod(LocalDate startDate, LocalDate endDate) {
            StringBuilder planPeriodBuilder = new StringBuilder();
            Function<LocalDate, String> dtConverter = date ->
                Objects.requireNonNull(date)
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    .substring(5);

            planPeriodBuilder.append(startDate.getYear())
                .append(".")
                .append(dtConverter.apply(startDate))
                .append(" - ");

            if (startDate.getYear() != endDate.getYear()) {
                planPeriodBuilder.append(endDate.getYear()).append(".");
            }

            return planPeriodBuilder.append(dtConverter.apply(endDate)).toString();
        }
    }


    @Getter
    @ToString
    @VisibleForTesting
    private static class CreatePlanRequest {

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
