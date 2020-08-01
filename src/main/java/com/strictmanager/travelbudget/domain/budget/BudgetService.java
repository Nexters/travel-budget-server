package com.strictmanager.travelbudget.domain.budget;

import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.persistence.jpa.BudgetRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long budgetId, Long amount) {
        final Budget budget = budgetRepository.findById(budgetId).orElseThrow(BudgetException::new);
        budget.changeAmount(amount);

        return budgetRepository.save(budget);
    }


    public Budget getPublicBudget(TripPlan plan) {
        return plan.getBudget();
    }

    public Budget getPersonalBudget(User user, TripPlan plan) {
        return tripMemberRepository.findByUserAndTripPlan(user, plan).orElseThrow()
            .getBudget();
    }


}
