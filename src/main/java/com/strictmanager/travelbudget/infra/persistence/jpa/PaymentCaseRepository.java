package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCaseRepository extends JpaRepository<PaymentCase, Long> {

    List<PaymentCase> findByBudget(Budget budget);

    List<PaymentCase> findByBudgetAndPaymentDtBetweenOrderByPaymentDtDesc(Budget budget, LocalDateTime startDt, LocalDateTime endDt);

    List<PaymentCase> findByBudgetAndPaymentDtIsNullOrderByCreateDtDesc(Budget budget);
}
