package com.strictmanager.travelbudget.domain.payment;

import com.strictmanager.travelbudget.application.payment.PaymentVO;
import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.payment.PaymentException.PaymentMessage;
import com.strictmanager.travelbudget.utils.LocalDateTimeUtils;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.infra.persistence.jpa.PaymentCaseRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCaseService {

    private final PaymentCaseRepository paymentCaseRepository;

    public PaymentCase getPaymentCase(Long paymentId) {
        return paymentCaseRepository.findById(paymentId).orElseThrow(() -> new PaymentException(
            PaymentMessage.CAN_NOT_FIND_PAYMENT));
    }

    public PaymentCase createPaymentCase(PaymentCase paymentCase) {
        return paymentCaseRepository.save(paymentCase);
    }

    public void deletePaymentCase(PaymentCase paymentCase) {
        paymentCaseRepository.delete(paymentCase);
    }

    public PaymentCase updatePaymentCase(Long userId, Long paymentId, PaymentVO paymentVO) {
        final PaymentCase paymentCase = paymentCaseRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(PaymentMessage.CAN_NOT_FIND_PAYMENT));
        if (!paymentCase.getCreateUser().getId().equals(userId)) {
            throw new PaymentException(PaymentMessage.EDIT_ONLY_MINE);
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
            budget, LocalDateTimeUtils.atStartOfDay(paymentDate), LocalDateTimeUtils.atMaxOfDay(paymentDate)
        );
    }

    public List<PaymentCase> getPaymentCaseByReady(Budget budget) {
        return paymentCaseRepository.findByBudgetAndIsReadyOrderByPaymentDtDesc(budget, YnFlag.Y);
    }
}
