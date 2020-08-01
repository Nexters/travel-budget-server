package com.strictmanager.travelbudget.domain.payment;

import org.springframework.stereotype.Service;

import com.strictmanager.travelbudget.infra.persistence.jpa.PaymentCaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentCaseService {

    private final PaymentCaseRepository paymentCaseRepository;

    public PaymentCase createPaymentCase(PaymentCase paymentCase) {
        return paymentCaseRepository.save(paymentCase);
    }
}
