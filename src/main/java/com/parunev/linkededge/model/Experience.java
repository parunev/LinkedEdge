package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Experience` class represents a user's work experience in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used during profile creation. This class holds information related to the user's job experience, including
 * the job description, job title, start and end dates, job location, and the organization associated with the job.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_EXPERIENCE")
@AttributeOverride(name = "id", column = @Column(name = "EXPERIENCE_ID"))
public class Experience extends BaseEntity {

    /**
     * The description of the job experience.
     */
    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    /**
     * The job title associated with the experience.
     */
    @Column(name = "JOB_TITLE")
    private String title;

    /**
     * The date when the user started the job.
     */
    @Column(name = "DATE_STARTED")
    private String dateStarted;

    /**
     * The date when the user ended the job.
     */
    @Column(name = "DATE_ENDED")
    private String dateEnded;

    /**
     * The location where the job was held.
     */
    @Column(name = "LOCATION")
    private String location;

    /**
     * The organization associated with the job experience.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_ORGANISATION_ID")
    private Organisation organisation;

    /**
     * The user profile to which this job experience is associated. These experiences can be added manually or imported from LinkedIn.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
