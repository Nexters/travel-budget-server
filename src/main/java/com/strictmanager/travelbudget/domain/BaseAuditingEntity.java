package com.strictmanager.travelbudget.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
public abstract class BaseAuditingEntity {

    @CreatedDate
    @Column(name = "create_dt", updatable = false, nullable = false)
    private LocalDateTime createDt;

    @LastModifiedDate
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;


}
