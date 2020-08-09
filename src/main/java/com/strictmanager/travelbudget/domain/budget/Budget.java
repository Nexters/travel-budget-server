package com.strictmanager.travelbudget.domain.budget;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budget")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class Budget extends BaseAuditingEntity {

    @Builder
    private Budget(
        Long createUserId,
        Long amount,
        Long paymentAmount
    ) {
        this.createUserId = requireNonNull(createUserId);
        this.amount = requireNonNull(amount);
        this.paymentAmount = requireNonNull(paymentAmount);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long createUserId;

    private Long amount;

    private Long paymentAmount;

    @OneToMany(mappedBy = "budget")
    private List<PaymentCase> paymentCases = new ArrayList<>();

    public Budget changeAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public Budget changePaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
        return this;
    }
}
