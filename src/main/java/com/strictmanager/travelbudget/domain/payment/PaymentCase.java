package com.strictmanager.travelbudget.domain.payment;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "payment_case")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class PaymentCase extends BaseAuditingEntity {

    @Builder
    private PaymentCase(
        Long price,
        String title,
        LocalDateTime paymentDt,
        PaymentCaseCategory category,
        Budget budget,
        User createUser,
        User updateUser,
        YnFlag isReady
    ) {
        this.price = requireNonNull(price);
        this.title = requireNonNull(title);
        this.paymentDt = requireNonNull(paymentDt);
        this.category = requireNonNull(category);
        this.budget = requireNonNull(budget);
        this.createUser = requireNonNull(createUser);
        this.updateUser = requireNonNull(updateUser);
        this.isReady = requireNonNull(isReady);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long price;

    @Column(length = 255)
    private String title;

    private LocalDateTime paymentDt;

    @Enumerated(EnumType.STRING)
    private PaymentCaseCategory category;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToOne
    @JoinColumn(name = "create_user_id")
    private User createUser;

    @OneToOne
    @JoinColumn(name = "update_user_id")
    private User updateUser;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="ENUM('Y','N')", nullable = false)
    private YnFlag isReady;

    public PaymentCase changeBudget(Budget budget) {
        this.budget = budget;
        return this;
    }

    public PaymentCase changePrice(Long price) {
        this.price = price;
        return this;
    }

    public PaymentCase changeTitle(String title) {
        this.title = title;
        return this;
    }

    public PaymentCase changePaymentDt(LocalDateTime paymentDt) {
        this.paymentDt = paymentDt;
        return this;
    }

    public PaymentCase changeCategory(PaymentCaseCategory category) {
        this.category = category;
        return this;
    }
}
