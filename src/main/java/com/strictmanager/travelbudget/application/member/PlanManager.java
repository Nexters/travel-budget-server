package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.domain.plan.PlanService;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.web.PlanController.PlanDetailResponse;
import com.strictmanager.travelbudget.web.PlanController.PlanResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanManager {

    private final PlanService planService;
    private final BudgetService budgetService;
    private final PaymentCaseService paymentCaseService;

    public List<PlanResponse> retrievePlans(User user, boolean isComing) {

        Stream<TripPlan> planStream;
        if (isComing) {
            List<TripPlan> doingPlans = planService.getDoingPlans(user);
            doingPlans.addAll(planService.getComingPlans(user));

            planStream = doingPlans.stream();
        } else {
            planStream = planService.getFinishPlans(user);
        }

        return planStream.map(plan -> PlanResponse.builder()
            .id(plan.getId())
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

    }

    public void createPlan(PlanVO vo) {
        planService.checkDateValidation(vo.getStartDate(), vo.getEndDate());

        Optional<Long> sharedBudgetOpt = Optional.ofNullable(vo.getSharedBudget());

        Budget budget = sharedBudgetOpt
            .map(amount -> budgetService.createBudget(Budget.builder()
                .amount(amount)
                .build()))
            .orElse(null);

        TripPlan tripPlan = planService.createPlan(TripPlan.builder()
            .name(vo.getName())
            .startDate(vo.getStartDate())
            .endDate(vo.getEndDate())
            .budget(budget)
            .userId(vo.getCreateUser().getId())
            .build());

        planService.createTripMember(TripMember.builder()
            .authority(Authority.OWNER)
            .tripPlan(tripPlan)
            .budget(budget)
            .user(vo.getCreateUser())
            .build());


    }

    public PlanDetailResponse retrievePlanDetail(User user, Long planId, LocalDate date) {
        Budget budget;
        TripPlan plan = planService.getPlan(planId);

        if (plan.getIsPublic().equals(YnFlag.Y)) {
            budget = budgetService.getPublicBudget(plan);
        } else {
            budget = budgetService.getPersonalBudget(user, plan);
        }

        List<PaymentCase> readyPaymentCase = paymentCaseService.getPaymentCaseByReady(budget);

        List<PaymentCase> paymentCases = Optional.ofNullable(date)
            .map(dt -> paymentCaseService.getPaymentCaseByDate(budget, dt))
            .orElse(readyPaymentCase);

        long readyUsedPrice = readyPaymentCase.stream().mapToLong(PaymentCase::getPrice).sum();

        int planDayCnt = plan.getStartDate().until(plan.getEndDate()).getDays() + 1;

        return PlanDetailResponse.builder()
            .purposeAmount(budget.getAmount())
            .suggestAmount(
                (double) ((budget.getAmount() - readyUsedPrice) / planDayCnt))
            .totalUseAmount(paymentCaseService.getPaymentUseAmount(budget))
            .dayUseAmount(paymentCases.stream().mapToLong(PaymentCase::getPrice).sum())
            .dates(getTripDates(plan.getStartDate(), planDayCnt))
            .paymentCases(convertPaymentVO(paymentCases))
            .build();
    }

    private List<PaymentVO> convertPaymentVO(List<PaymentCase> paymentCases) {
        return paymentCases.stream()
            .map(paymentCase -> PaymentVO.builder()
                .id(paymentCase.getId())
                .title(paymentCase.getTitle())
                .price(paymentCase.getPrice())
                .category(paymentCase.getCategory())
                .build())
            .collect(Collectors.toList());
    }

    private List<LocalDate> getTripDates(LocalDate startDate, int planDaysCnt) {
        List<LocalDate> tripDates = new ArrayList<>();
        LocalDate targetDate = startDate;

        for (int i = 1; i < planDaysCnt; i++) {
            tripDates.add(targetDate);
            targetDate = targetDate.plusDays(1);
        }
        return tripDates;
    }
}
