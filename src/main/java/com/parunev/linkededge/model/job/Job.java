package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.Profile;
import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * The `Job` class represents a job in the LinkedEdge application, typically extracted from a LinkedIn job post. It extends the `BaseEntity` class
 * and includes various fields related to the job, such as job description, employment status, job title, location, job posting URL, and job posting ID.
 * This class is associated with job functions and job industries.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_JOB")
@AttributeOverride(name = "id", column = @Column(name = "JOB_ID"))
public class Job extends BaseEntity {

    /**
     * The job description.
     */
    @Column(name = "JOB_DESCRIPTION", length = 30000)
    private String jobDescription;

    /**
     * The employment status related to the job.
     */
    @Column(name = "EMPLOYMENT_STATUS")
    private String employmentStatus;

    /**
     * The title of the job.
     */
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    /**
     * The location of the job.
     */
    @Column(name = "JOB_LOCATION")
    private String location;

    /**
     * The LinkedIn URL of the job posting.
     */
    @Column(name = "JOB_POSTING_URL")
    private String jobPostingUrl;

    /**
     * The ID associated with the job posting.
     */
    @Column(name = "JOB_POSTING_ID")
    private String jobPostingId;

    /**
     * A list of job functions associated with this job.
     */
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobFunction> functions;

    /**
     * A list of job industries associated with this job.
     */
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobIndustry> industries;

    /**
     * The user profile associated with this job.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
