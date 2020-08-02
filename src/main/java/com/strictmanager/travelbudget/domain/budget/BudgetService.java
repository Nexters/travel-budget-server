package com.strictmanager.travelbudget.domain.budget;

import com.strictmanager.travelbudget.infra.persistence.jpa.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

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
}
