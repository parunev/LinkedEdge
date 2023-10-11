package com.parunev.linkededge.model.commons;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * The `BaseEntity` class is an abstract base class for entities in the LinkedEdge application. It provides a universally unique identifier (UUID) as the primary key for identifying entities.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity extends BaseAudit<String> implements Serializable{

    /**
     * The universally unique identifier (UUID) serving as the primary key for identifying entities.
     */
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
