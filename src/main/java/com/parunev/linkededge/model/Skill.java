package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_SKILL")
@AttributeOverride(name = "id", column = @Column(name = "SKILL_ID"))
public class Skill extends BaseEntity {

    @Column(name = "SKILL_NAME")
    private String name;

    @Column(name = "NUMBER_OF_ENDORSEMENT")
    private Integer numOfEndorsement;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
