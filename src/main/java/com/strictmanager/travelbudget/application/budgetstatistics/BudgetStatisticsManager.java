package com.strictmanager.travelbudget.application.budgetstatistics;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.budget.BudgetService;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.payment.PaymentCaseCategory;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetStatisticsManager {

    private final BudgetService budgetService;

    public BudgetStatisticsVO getStatics(Long budgetId) {
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

        return BudgetStatisticsVO.builder()
            .purposeAmount(budget.getAmount())
            .usedAmount(budget.getPaymentAmount())
            .categories(categoryMap)
            .build();
    }
}
