package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Skill` class represents a skill in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used when creating a user profile. These skills can be sourced from the user's LinkedIn profile or
 * added manually by the user.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_SKILL")
@AttributeOverride(name = "id", column = @Column(name = "SKILL_ID"))
public class Skill extends BaseEntity {

    /**
     * The name of the skill.
     */
    @Column(name = "SKILL_NAME")
    private String name;

    /**
     * The number of endorsements received for this skill.
     */
    @Column(name = "NUMBER_OF_ENDORSEMENT")
    private Integer numOfEndorsement;

    /**
     * The user profile to which this skill is associated.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
