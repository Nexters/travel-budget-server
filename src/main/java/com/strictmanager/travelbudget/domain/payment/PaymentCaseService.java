package com.strictmanager.travelbudget.domain.payment;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.infra.persistence.jpa.PaymentCaseRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCaseService {

    private final PaymentCaseRepository paymentCaseRepository;

    public PaymentCase createPaymentCase(PaymentCase paymentCase) {
        return paymentCaseRepository.save(paymentCase);
    }

    public List<PaymentCase> getPaymentCase(Budget budget) {
        return paymentCaseRepository.findByBudget(budget);
    }

    public List<PaymentCase> getPaymentCaseByDate(Budget budget, LocalDate localDate) {
        return paymentCaseRepository.findByBudgetAndPaymentDate(budget, localDate)
            .sorted(Comparator.comparing(PaymentCase::getPaymentTime).reversed())
            .collect(Collectors.toList());
    }

    public List<PaymentCase> getPaymentCaseByReady(Budget budget) {
        return paymentCaseRepository.findByBudgetAndPaymentDateIsNull(budget)
            .sorted(Comparator.comparing(PaymentCase::getCreateDt).reversed())
            .collect(Collectors.toList());
    }

    public long getPaymentUseAmount(Budget budget) {
        return paymentCaseRepository.findByBudget(budget)
            .stream()
            .mapToLong(PaymentCase::getPrice)
            .sum();
    }
}
