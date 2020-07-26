package com.strictmanager.travelbudget.domain.plan.service;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.plan.TripPlan.YnFlag;
import com.strictmanager.travelbudget.infra.persistence.jpa.BudgetRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripPlanRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public List<TripPlan> getPlans(Long userId) {
        return tripMemberRepository.findByUser_Id(userId)
            .stream()
            .map(TripMember::getTripPlan)
            .filter(tripPlan -> tripPlan.getIsDelete().equals(YnFlag.N))
            .sorted(Comparator.comparing(TripPlan::getStartDate).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public TripPlan createPlan(TripPlan tripPlan) {
        return tripPlanRepository.save(tripPlan);
    }

    @Override
    public TripPlan createNewPlan(Long userId) {
        return null;
    }
}
