package com.strictmanager.travelbudget.domain.plan;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import com.strictmanager.travelbudget.domain.YnFlag;
import com.strictmanager.travelbudget.domain.budget.Budget;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "trip_plan")
@DynamicInsert
@DynamicUpdate
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
    @Column(columnDefinition = "ENUM('Y','N')", nullable = false)
    private YnFlag isPublic;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Y','N')", nullable = false)
    private YnFlag isDelete = YnFlag.N;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToMany(mappedBy = "tripPlan")
    private List<TripMember> tripMembers = new ArrayList<>();

    @Builder
    public TripPlan(String name, LocalDate startDate, LocalDate endDate, Long userId, Budget budget,
        YnFlag isPublic) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createUserId = userId;
        this.updateUserId = userId;
        this.isPublic = isPublic;

        if (isPublic.equals(YnFlag.Y)) {
            this.budget = Objects.requireNonNull(budget);
        }
    }

    public TripPlan updateName(String name, Long userId) {
        if (ObjectUtils.notEqual(this.getName(), name)) {
            this.name = name;
            this.updateUserId = userId;
        }
        return this;
    }

    public TripPlan deletePlan() {
        this.isDelete = YnFlag.Y;
        return this;
    }

}
