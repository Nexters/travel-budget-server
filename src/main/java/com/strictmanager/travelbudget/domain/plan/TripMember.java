package com.strictmanager.travelbudget.domain.plan;


import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.budget.Budget;
import com.strictmanager.travelbudget.domain.user.User;
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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class TripMember extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="ENUM('OWNER', 'MEMBER')", nullable = false)
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "trip_plan_id")
    private TripPlan tripPlan;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "budget_id")
    private Budget budget;


    public enum Authority {
        OWNER, MEMBER
    }

    public TripMember updateBudget(Budget budget) {
        this.budget = budget;
        return this;
    }

    @Builder
    public TripMember(Authority authority, TripPlan tripPlan, User user, Budget budget) {
        this.authority = authority;
        this.tripPlan = tripPlan;
        this.user = user;
        this.budget = budget;
    }


}
