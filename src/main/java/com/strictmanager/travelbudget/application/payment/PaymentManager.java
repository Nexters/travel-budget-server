package com.strictmanager.travelbudget.application.payment;

import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.domain.user.UserService;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentManager {

    private final PaymentCaseService paymentCaseService;
    private final BudgetService budgetService;
    private final UserService userService;

    public List<PaymentCase> getPaymentCases(Long userId, Long budgetId, YnFlag isReady,
        LocalDate paymentDate) {
        final Budget budget = budgetService.getBudget(budgetId);
        if (YnFlag.Y == isReady) {
            return paymentCaseService.getPaymentCaseByReady(budget);
        }

        return paymentCaseService.getPaymentCaseByDate(budget, paymentDate);
    }

    @Transactional
    public Long createPaymentCase(PaymentVO paymentVO) {
        final User user = userService.getUser(paymentVO.getUserId());
        final Budget budget = budgetService.getBudget(paymentVO.getBudgetId());
        final PaymentCase paymentCase = paymentCaseService.createPaymentCase(
            PaymentCase.builder()
                .price(paymentVO.getPrice())
                .title(paymentVO.getTitle())
                .paymentDt(paymentVO.getPaymentDt())
                .category(paymentVO.getPaymentCaseCategory())
                .budget(budget)
                .createUser(user)
                .updateUser(user)
                .isReady(paymentVO.getIsReady())
                .build()
        );

        final Long updatedBudgetPaymentAmount = budget.getPaymentAmount() + paymentVO.getPrice();
        budgetService.updateBudgetPaymentAmount(
            paymentVO.getUserId(), paymentVO.getBudgetId(), updatedBudgetPaymentAmount
        );

        return paymentCase.getId();
    }

    @Transactional
    public Long updatePaymentCase(Long userId, Long paymentId, PaymentVO paymentVO) {
        final PaymentCase paymentCase = paymentCaseService.getPaymentCase(paymentId);
        final Budget originBudget = paymentCase.getBudget();
        final Long originPrice = paymentCase.getPrice();
        final Long updatedPrice = paymentVO.getPrice();

        Budget budget;

        final Long updatedBudgetPaymentAmount;
        if (ObjectUtils.notEqual(originBudget.getId(), paymentVO.getBudgetId())) {
            budgetService
                .updateBudgetPaymentAmount(userId, originBudget.getId(),
                    originBudget.getPaymentAmount() - originPrice);

            budget = budgetService.getBudget(paymentVO.getBudgetId());

            updatedBudgetPaymentAmount = budget.getPaymentAmount() + updatedPrice;
        } else {
            updatedBudgetPaymentAmount =
                originBudget.getPaymentAmount() - originPrice + updatedPrice;

            budget = originBudget;
        }

        paymentCaseService.updatePaymentCase(
            userId,
            paymentId,
            budget,
            paymentVO
        );

        budgetService.updateBudgetPaymentAmount(
            paymentVO.getUserId(), budget.getId(), updatedBudgetPaymentAmount
        );

        return paymentId;
    }

    public void deletePaymentCase(Long paymentId) {
        PaymentCase paymentCase = paymentCaseService.getPaymentCase(paymentId);

        Budget budget = paymentCase.getBudget();

        budgetService.saveBudget(
            budget.changePaymentAmount(budget.getPaymentAmount() - paymentCase.getPrice()));

        paymentCaseService.deletePaymentCase(paymentCase);
    }
}
