package com.strictmanager.travelbudget.domain.budget;

import com.strictmanager.travelbudget.infra.persistence.jpa.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long budgetId, Long amount) {
        final Budget budget = budgetRepository.findById(budgetId).orElseThrow(BudgetException::new);
        budget.changeAmount(amount);

        return budgetRepository.save(budget);
    }
}
