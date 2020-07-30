package com.strictmanager.travelbudget.web;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strictmanager.travelbudget.application.member.MemberBudgetManager;
import com.strictmanager.travelbudget.application.member.PlanManager;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.plan.PlanVO;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.domain.plan.service.PlanService;
import com.strictmanager.travelbudget.domain.user.User;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

    private final BudgetService budgetService;
    private final PlanService planService;
    private final PaymentCaseService paymentCaseService;


    private final PlanManager planManager;
    private final MemberBudgetManager memberBudgetManager;

    @GetMapping("/plans")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PlanResponse>> retrievePlans(
        @AuthenticationPrincipal User user,
        @RequestParam(name = "isComing") boolean isComing) {
        List<PlanResponse> responses = planManager.retrievePlans(user, isComing);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/plans")
    public ResponseEntity createPlan(@AuthenticationPrincipal User user,
        HttpServletRequest httpServletRequest,
        @RequestBody CreatePlanRequest param) {

        planManager.createPlan(PlanVO.builder()
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
    public ResponseEntity<PlanDetailResponse> planDetail(@AuthenticationPrincipal User user,
        @PathVariable(value = "id") Long planId,
        @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        Budget budget;
        TripPlan plan = planService.getPlan(planId);

        if (plan.getIsPublic().equals(YnFlag.Y)) {
            budget = budgetService.getPublicBudget(plan);
        } else {
            budget = budgetService.getPersonalBudget(user, plan);
        }

        Optional<LocalDate> dateOptional = Optional.ofNullable(date);
        List<PaymentCase> paymentCases = dateOptional
            .map(dt -> paymentCaseService.getPaymentCaseByDate(budget, dt))
            .orElseGet(() -> paymentCaseService.getPaymentCaseByReady(budget));

        long readyDuringSum = paymentCaseService.getPaymentCaseByReady(budget)
            .stream().mapToLong(PaymentCase::getPrice)
            .sum();

        Period period = plan.getStartDate().until(plan.getEndDate());

        PlanDetailResponse response = PlanDetailResponse.builder()
            .purposeAmount(budget.getAmount())
            .suggestAmount(
                (double) ((budget.getAmount() - readyDuringSum) / (period.getDays() + 1)))
            .totalUseAmount(paymentCaseService.getPaymentUseAmount(budget))
            .dayUseAmount(paymentCases.stream()
                .mapToLong(PaymentCase::getPrice).sum())
            .dates(new ArrayList<>()) // TODO:  2020-07-31 (kiyeon_kim1)
            .paymentCases(new ArrayList<>()) // TODO: 수정 예 2020-07-31 (kiyeon_kim1)
            .build();

        return ResponseEntity.ok(response);
    }

    @Getter
    public static class PlanDetailResponse {

        private final Long purposeAmount; //전체예산
        private final Double suggestAmount; // 일자별 제안 예
        private final Long totalUseAmount; // 사용된 예산
        private final Long dayUseAmount; // 일자별 사용된 예산

        private final List<LocalDate> dates;
        private final List<PaymentCase> paymentCases;

        @Builder
        public PlanDetailResponse(Long purposeAmount, Double suggestAmount, Long totalUseAmount,
            TripPlan plan,
            Long dayUseAmount, List<LocalDate> dates,
            List<PaymentCase> paymentCases) {

            this.purposeAmount = purposeAmount;
            this.suggestAmount = suggestAmount;
            this.totalUseAmount = totalUseAmount;
            this.dayUseAmount = dayUseAmount;

            this.dates = dates;
            this.paymentCases = paymentCases;
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
