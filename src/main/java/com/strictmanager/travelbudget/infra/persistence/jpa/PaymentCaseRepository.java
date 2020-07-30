package com.strictmanager.travelbudget.infra.persistence.jpa;

import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCaseRepository extends JpaRepository<PaymentCase, Long> {

    List<PaymentCase> findByBudget(Budget budget);

    Stream<PaymentCase> findByBudgetAndPaymentDate(Budget budget, LocalDate date);

    Stream<PaymentCase> findByBudgetAndPaymentDateIsNull(Budget budget);

}
