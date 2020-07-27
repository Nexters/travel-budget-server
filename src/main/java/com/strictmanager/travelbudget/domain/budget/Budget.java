package com.strictmanager.travelbudget.domain.budget;

import static java.util.Objects.requireNonNull;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @Builder(toBuilder = true)
    private Budget(
        Long amount
    ) {
        this.amount = requireNonNull(amount);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    public void changeAmount(Long amount) {
        this.amount = amount;
    }
}
