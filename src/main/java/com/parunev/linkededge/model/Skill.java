package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_SKILL")
@AttributeOverride(name = "id", column = @Column(name = "SKILL_ID"))
public class Skill extends BaseEntity {

    @Column(name = "SKILL_NAME")
    private String name;

    @Column(name = "NUMBER_OF_ENDORSEMENT")
    private Integer numOfEndorsement;
}
