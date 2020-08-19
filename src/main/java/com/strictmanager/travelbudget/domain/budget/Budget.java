package com.strictmanager.travelbudget.domain.budget;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.payment.PaymentCase;
import com.strictmanager.travelbudget.domain.plan.TripPlan;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "budget")
@DynamicUpdate
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

    @Nullable
    @OneToOne(mappedBy = "budget")
    private TripPlan plan;


    @OneToMany(mappedBy = "budget", cascade = CascadeType.REMOVE)
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
