package com.strictmanager.travelbudget.domain.budget;

import com.strictmanager.travelbudget.domain.budget.BudgetException.BudgetMessage;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.infra.persistence.jpa.BudgetRepository;
import com.strictmanager.travelbudget.infra.persistence.jpa.TripMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TripMemberRepository tripMemberRepository;

    public Budget getBudget(Long budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(() -> new BudgetException(
            BudgetMessage.CAN_NOT_FIND_BUDGET));
    }

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget updateBudgetPaymentAmount(Long userId, Long budgetId, Long paymentAmount) {
        final Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new BudgetException(BudgetMessage.CAN_NOT_FIND_BUDGET));
        if (!budget.getCreateUserId().equals(userId)) {
            throw new BudgetException(BudgetMessage.EDIT_ONLY_MINE);
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
