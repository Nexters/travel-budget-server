package com.strictmanager.travelbudget.infra.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.strictmanager.travelbudget.domain.payment.PaymentCase;

@Repository
public interface PaymentCaseRepository extends JpaRepository<PaymentCase, Long> {

}
