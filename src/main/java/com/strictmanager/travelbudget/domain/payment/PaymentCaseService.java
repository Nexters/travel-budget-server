package com.strictmanager.travelbudget.domain.payment;

import com.strictmanager.travelbudget.application.payment.PaymentVO;
import org.springframework.stereotype.Service;

import com.strictmanager.travelbudget.infra.persistence.jpa.PaymentCaseRepository;

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
        final PaymentCase paymentCase = paymentCaseRepository.findById(paymentId).orElseThrow(PaymentException::new);
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
}
