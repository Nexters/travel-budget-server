package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.plan.PlanService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanManager {

    private final PlanService planService;
    private final BudgetService budgetService;
    private final PaymentCaseService paymentCaseService;

    private final long INIT_AMOUNT = 0L;

    public List<PlanResponse> getPlans(User user, boolean isComing) {

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

    @Transactional
    public void createPlan(PlanVO vo) {
        planService.checkDateValidation(vo.getStartDate(), vo.getEndDate());

        Optional<Long> sharedBudgetOpt = Optional.ofNullable(vo.getSharedBudget());

        Budget budget = sharedBudgetOpt
            .map(amount -> budgetService.createBudget(Budget.builder()
                .createUserId(vo.getCreateUser().getId())
                .paymentAmount(INIT_AMOUNT)
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

    public PlanDetailResponse getPlanDetail(User user, Long planId) {
        Budget budget;
        TripPlan plan = planService.getPlan(planId);

        if (plan.getIsPublic().equals(YnFlag.Y)) {
            budget = budgetService.getPublicBudget(plan);
        } else {
            budget = budgetService.getPersonalBudget(user, plan);
        }

        long readyUsedPrice = paymentCaseService.getPaymentCaseByReady(budget)
            .stream()
            .mapToLong(PaymentCase::getPrice)
            .sum();

        int planDayCnt = plan.getStartDate().until(plan.getEndDate()).getDays() + 1;

        return PlanDetailResponse.builder()
            .purposeAmount(budget.getAmount())
            .suggestAmount(
                (double) ((budget.getAmount() - readyUsedPrice) / planDayCnt))
            .totalUseAmount(paymentCaseService.getPaymentUseAmount(budget))
            .dates(getTripDates(plan.getStartDate(), planDayCnt))
            .build();
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
