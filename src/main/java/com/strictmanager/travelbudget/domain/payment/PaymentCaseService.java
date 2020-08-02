package com.strictmanager.travelbudget.domain.payment;

import com.strictmanager.travelbudget.application.payment.PaymentVO;
import org.springframework.stereotype.Service;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.infra.persistence.jpa.PaymentCaseRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentCaseService {

    private final PaymentCaseRepository paymentCaseRepository;

    public PaymentCase getPaymentCase(Long paymentId) {
        return paymentCaseRepository.findById(paymentId).orElseThrow(PaymentException::new);
    }

    public PaymentCase createPaymentCase(PaymentCase paymentCase) {
        return paymentCaseRepository.save(paymentCase);
    }

    public PaymentCase updatePaymentCase(Long userId, Long paymentId, PaymentVO paymentVO) {
        final PaymentCase paymentCase = paymentCaseRepository.findById(paymentId)
            .orElseThrow(PaymentException::new);
        if (!paymentCase.getCreateUser().getId().equals(userId)) {
            throw new PaymentException();
        }

        return paymentCaseRepository.save(
            paymentCase.changePrice(paymentVO.getPrice())
                .changeTitle(paymentVO.getTitle())
                .changePaymentDt(paymentVO.getPaymentDt())
                .changeCategory(paymentVO.getPaymentCaseCategory())
        );
    }

    public List<PaymentCase> getPaymentCase(Budget budget) {
        return paymentCaseRepository.findByBudget(budget);
    }

    public List<PaymentCase> getPaymentCaseByDate(Budget budget, LocalDate localDate) {
//        return paymentCaseRepository.findByBudgetAndPaymentDate(budget, localDate)
        return paymentCaseRepository.findByBudgetAndPaymentDt(budget, localDate)
            .sorted(Comparator.comparing(PaymentCase::getPaymentDt).reversed())
//            .sorted(Comparator.comparing(PaymentCase::getPaymentTime).reversed())
            // TODO: 동작 확인 필요 2020-08-02 (kiyeon_kim1)
            .collect(Collectors.toList());
    }

    public List<PaymentCase> getPaymentCaseByReady(Budget budget) {
//        return paymentCaseRepository.findByBudgetAndPaymentDateIsNull(budget)
        return paymentCaseRepository.findByBudgetAndPaymentDtIsNull(budget)
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
