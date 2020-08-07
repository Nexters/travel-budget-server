package com.strictmanager.travelbudget.application.payment;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseService;
import com.strictmanager.travelbudget.domain.payment.PaymentException;
import com.strictmanager.travelbudget.domain.user.User;
import com.strictmanager.travelbudget.domain.user.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentManager {

    private final PaymentCaseService paymentCaseService;
    private final BudgetService budgetService;
    private final UserService userService;

    public List<PaymentCase> getPaymentCases(Long userId, Long budgetId, LocalDate paymentDate) {
        final Budget budget = budgetService.getBudget(budgetId);
        if (!budget.getCreateUserId().equals(userId)) {
            throw new PaymentException();
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
        final Budget budget = paymentCase.getBudget();
        final Long originPrice = paymentCase.getPrice();
        final Long updatedPrice = paymentVO.getPrice();

        paymentCaseService.updatePaymentCase(
            userId,
            paymentId,
            paymentVO
        );

        final Long updatedBudgetPaymentAmount = budget.getPaymentAmount() - originPrice + updatedPrice;
        budgetService.updateBudgetPaymentAmount(
            paymentVO.getUserId(), paymentVO.getBudgetId(), updatedBudgetPaymentAmount
        );

        return paymentId;
    }
}
