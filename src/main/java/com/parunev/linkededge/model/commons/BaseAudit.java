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

/**
 * The `BaseAudit` class is an abstract base class that is used for auditing and tracking entity-related information in the LinkedEdge application.
 * It provides fields for recording the user who created and modified an entity, along with timestamps for creation and modification.
 *
 * @param <U> The type of user or entity responsible for the actions (e.g., user, admin).
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAudit<U> {

    /**
     * The user responsible for creating the entity.
     */
    @CreatedBy
    @Column(name = "CREATED_BY_USER", length = 200)
    protected U createdByUser;

    /**
     * The timestamp when the entity was created.
     */
    @CreatedDate
    @Column(name = "CREATION_TIMESTAMP")
    protected LocalDate creationTimestamp;

    /**
     * The user responsible for the last modification of the entity.
     */
    @LastModifiedBy
    @Column(name = "MODIFIED_BY_USER", length = 200)
    protected U lastModifiedByUser;

    /**
     * The timestamp of the last modification of the entity.
     */
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_TIMESTAMP")
    protected LocalDate lastModifiedTimestamp;
}
