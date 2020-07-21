package com.strictmanager.travelbudget.domain.outlay;

import com.strictmanager.travelbudget.domain.BaseAuditingEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budget")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class Budget extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long purposeBudget; // TODO: 해당 컬럼 명 변경하는게 어떨까요? payment_case 테이블에서도 조인해서 쓰기 때문에, 적절 치 않다고 보여짐

}
