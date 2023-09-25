package com.parunev.linkededge.model.commons;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAudit<U> {
    @CreatedBy
    @Column(name = "CREATED_BY_USER", length = 200)
    protected U createdByUser;

    @CreatedDate
    @Column(name = "CREATION_TIMESTAMP")
    protected LocalDate creationTimestamp;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY_USER", length = 200)
    protected U lastModifiedByUser;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_TIMESTAMP")
    protected LocalDate lastModifiedTimestamp;
}
