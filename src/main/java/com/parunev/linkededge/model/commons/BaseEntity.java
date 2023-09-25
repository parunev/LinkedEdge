package com.parunev.linkededge.model.commons;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity extends BaseAudit<String> implements Serializable{

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
