package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.plan.PlanVO;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripMember.Authority;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.service.PlanService;
import com.strictmanager.travelbudget.web.PlanController.PlanResponse;
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

    public List<PlanResponse> retrievePlans(Long userId, boolean isComing) {

        Stream<TripPlan> planStream;
        if (isComing) {
            List<TripPlan> doingPlans = planService.getDoingPlans(userId);
            doingPlans.addAll(planService.getComingPlans(userId));

            planStream = doingPlans.stream();
        } else {
            planStream = planService.getFinishPlans(userId);
        }

        return planStream.map(plan -> PlanResponse.builder()
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
}
