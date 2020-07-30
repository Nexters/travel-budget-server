package com.strictmanager.travelbudget.domain.payment;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.CascadeType;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_case")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class PaymentCase extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long price;

    @Column(length = 255)
    private String title;

    private LocalDate paymentDate;
    private LocalTime paymentTime;


    @Enumerated(EnumType.STRING)
    private PaymentCaseCategory category;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "create_user_id")
    private User createUser;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "update_user_id")
    private User updateUser;
}
