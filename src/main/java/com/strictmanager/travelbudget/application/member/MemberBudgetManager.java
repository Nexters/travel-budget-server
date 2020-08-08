package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.member.MemberService;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberBudgetManager {

    private final MemberService memberService;
    private final BudgetService budgetService;

    @Transactional
    public Long createMemberBudget(BudgetVO budgetVO) {
        final Budget budget = budgetService.createBudget(
            Budget.builder()
                .createUserId(budgetVO.getUser().getId())
                .amount(budgetVO.getAmount())
                .paymentAmount(0L)
                .build()
        );

        TripMember member = memberService.getMember(budgetVO.getMemberId());

        memberService.saveMember(member.updateBudget(budget));

        return budget.getId();
    }

}
