package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberBudgetManager {

    private final BudgetService budgetService;

    @Transactional
    public Long createMemberBudget(BudgetVO budgetVO) {
        final Budget budget = budgetService.createBudget(
            Budget.builder()
                .amount(budgetVO.getAmount())
                .build()
        );

        // TODO: update trip_member (budget_id)

        return budget.getId();
    }

}
