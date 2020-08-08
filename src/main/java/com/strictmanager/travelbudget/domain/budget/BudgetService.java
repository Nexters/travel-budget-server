package com.strictmanager.travelbudget.domain.budget;

import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.persistence.jpa.BudgetRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripPlanRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TripPlanRepository tripPlanRepository;
    private final TripMemberRepository tripMemberRepository;

    public Budget getBudget(Long budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(BudgetException::new);
    }

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget updateBudgetAmount(Long userId, Long budgetId, Long amount) {
        final Budget budget = budgetRepository.findById(budgetId).orElseThrow(BudgetException::new);
        if (!budget.getCreateUserId().equals(userId)) {
            throw new BudgetException();
        }

        return budgetRepository.save(budget.changeAmount(amount));
    }

    public Budget updateBudgetPaymentAmount(Long userId, Long budgetId, Long paymentAmount) {
        final Budget budget = budgetRepository.findById(budgetId).orElseThrow(BudgetException::new);
        if (!budget.getCreateUserId().equals(userId)) {
            throw new BudgetException();
        }

        return budgetRepository.save(budget.changePaymentAmount(paymentAmount));
    }


    public Optional<Budget> getPublicBudget(TripPlan plan) {
        return Optional.ofNullable(plan.getBudget());
    }

    public Optional<Budget> getPersonalBudget(User user, TripPlan plan) {
        return tripMemberRepository.findByUserAndTripPlan(user, plan)
            .map(TripMember::getBudget);
    }

}
