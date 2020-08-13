package com.strictmanager.travelbudget.application.member;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetException;
import com.strictmanager.travelbudget.domain.budget.BudgetException.BudgetMessage;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.member.MemberService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import com.strictmanager.travelbudget.domain.plan.TripMember;
import com.strictmanager.travelbudget.web.PlanBudgetController.BudgetStaticResponse;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
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


    public Budget updateBudgetAmount(Long userId, Long budgetId, Long amount) {
        Budget budget = budgetService.getBudget(budgetId);

        if (!budget.getCreateUserId().equals(userId)) {
            throw new BudgetException(BudgetMessage.EDIT_ONLY_MINE);
        }

        return budgetService.saveBudget(budget.changeAmount(amount));
    }

    @Transactional
    public BudgetStaticResponse getStatics(Long budgetId) {
        Budget budget = budgetService.getBudget(budgetId);

        EnumMap<PaymentCaseCategory, Long> categoryMap = new EnumMap<>(
            PaymentCaseCategory.class);

        Arrays.stream(PaymentCaseCategory.values()).forEach(key -> categoryMap.put(key, 0L));

        List<PaymentCase> paymentCases = budget.getPaymentCases();

        paymentCases.forEach(paymentCase -> {
            PaymentCaseCategory key = paymentCase.getCategory();
            Long sumPrice = paymentCase.getPrice() + categoryMap.get(key);
            categoryMap.put(key, sumPrice);
        });

        return BudgetStaticResponse.builder()
            .purposeAmount(budget.getAmount())
            .usedAmount(budget.getPaymentAmount())
            .categories(categoryMap)
            .build();
    }
}
