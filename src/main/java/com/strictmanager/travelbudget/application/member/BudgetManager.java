package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetException;
import com.strictmanager.travelbudget.domain.budget.BudgetException.BudgetMessage;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.member.MemberService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetManager {

    private final MemberService memberService;
    private final BudgetService budgetService;

    @Transactional
    public Long createMemberBudget(BudgetVO budgetVO) {
        final Budget budget = budgetService.saveBudget(
            Budget.builder()
                .createUserId(budgetVO.getUserId())
                .amount(budgetVO.getAmount())
                .paymentAmount(0L)
                .build()
        );

        TripMember member = memberService.getMember(budgetVO.getMemberId());

        memberService.saveMember(member.updateBudget(budget));

        return budget.getId();
    }

    @Transactional
    public Budget updateBudgetAmount(Long userId, Long budgetId, Long amount) {
        Budget budget = budgetService.getBudget(budgetId);

        if (!budget.getCreateUserId().equals(userId)) {
            throw new BudgetException(BudgetMessage.EDIT_ONLY_MINE);
        }

        return budgetService.saveBudget(budget.changeAmount(amount));
    }
}
