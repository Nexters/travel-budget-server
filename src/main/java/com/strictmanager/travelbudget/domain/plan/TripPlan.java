package com.strictmanager.travelbudget.domain.plan;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.budget.Budget;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "trip_plan")
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class TripPlan extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String name;

    private LocalDate startDate;
    private LocalDate endDate;

    private Long createUserId;
    private Long updateUserId;

    @Enumerated(EnumType.STRING)
    private YnFlag isPublic;

    @Enumerated(EnumType.STRING)
    private YnFlag isDelete;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    public enum YnFlag {
        Y, N
    }

    @Builder
    public TripPlan(String name, LocalDate startDate, LocalDate endDate, Long userId, Budget budget) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createUserId = userId;
        this.updateUserId = userId;

        setCaseIsPublic(budget);

    }

    private void setCaseIsPublic(Budget budget) {
        if(budget != null) {
            this.isPublic = YnFlag.Y;
            this.budget = Objects.requireNonNull(budget);
        } else {
            this.budget = null;
            this.isPublic = YnFlag.N;
        }
    }
}
