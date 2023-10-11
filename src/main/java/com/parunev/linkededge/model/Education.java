package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Education` class represents a user's educational background in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used during profile creation. This class holds information related to the user's education, including the institution
 * name, degree earned, field of study, start and end dates of education.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_EDUCATION")
@AttributeOverride(name = "id", column = @Column(name = "EDUCATION_ID"))
public class Education extends BaseEntity {

    /**
     * The name of the educational institution.
     */
    @Column(name = "INSTITUTION_NAME")
    private String institutionName;

    /**
     * The degree earned by the user.
     */
    @Column(name = "DEGREE")
    private String degree;

    /**
     * The field of study or major associated with the education.
     */
    @Column(name = "FIELD_OF_STUDY")
    private String fieldOfStudy;

    /**
     * The date when the user started their education.
     */
    @Column(name = "DATE_STARTED")
    private String dateStarted;

    /**
     * The date when the user completed their education.
     */
    @Column(name = "DATE_ENDED")
    private String dateEnded;

    /**
     * The user profile to which this education is associated. These education details can be added manually during profile creation.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
