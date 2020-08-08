package com.strictmanager.travelbudget.domain.payment;

import com.strictmanager.travelbudget.application.payment.PaymentVO;
import com.strictmanager.travelbudget.domain.YnFlag;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.infra.persistence.jpa.PaymentCaseRepository;
import java.time.LocalDate;
import java.util.List;
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

    public List<PaymentCase> getPaymentCaseByDate(Budget budget, LocalDate paymentDate) {
        return paymentCaseRepository.findByBudgetAndPaymentDtBetweenOrderByPaymentDtDesc(
            budget, paymentDate.atStartOfDay(), paymentDate.atTime(LocalTime.MAX)
        );
    }

    public List<PaymentCase> getPaymentCaseByReady(Budget budget) {
        return paymentCaseRepository.findByBudgetAndIsReadyOrderByPaymentDtDesc(budget, YnFlag.Y);
    }

    public long getPaymentUseAmount(Budget budget) {
        return paymentCaseRepository.findByBudget(budget)
            .stream()
            .mapToLong(PaymentCase::getPrice)
            .sum();
    }
}
