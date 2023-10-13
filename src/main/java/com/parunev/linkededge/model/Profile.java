package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.job.Job;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * The `Profile` class represents a user profile in the LinkedEdge application. It extends the `BaseEntity` class,
 * providing various details related to a user's profile, such as credits, education, experience, skills, and more.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_PROFILE")
@AttributeOverride(name = "id", column = @Column(name = "PROFILE_ID"))
public class Profile extends BaseEntity {

    /**
     * The number of general credits associated with the user's profile. Users start with 3 general credits.
     * These credits can be used for generating random interview questions or to generate specific answer.
     */
    @Column(name = "CREDITS")
    private Integer credits;

    /**
     * The number of job-specific credits associated with the user's profile. These credits are specifically
     * allocated for the "Preparation for job" functionality.
     */
    @Column(name = "JOB_CREDITS")
    private Integer jobCredits;

    /**
     * The extra capacity for education on the user's profile. Users may have the option to purchase extra
     * capacity to add more education entries manually.
     */
    @Column(name = "EDUCATION_EXTRA_CAPACITY")
    private Integer educationExtraCapacity;

    /**
     * The extra capacity for experience on the user's profile. Users may have the option to purchase extra
     * capacity to add more experience entries manually.
     */
    @Column(name = "EXPERIENCE_EXTRA_CAPACITY")
    private Integer experienceExtraCapacity;

    /**
     * The extra capacity for skills on the user's profile. Users may have the option to purchase extra
     * capacity to add more skills manually.
     */
    @Column(name = "SKILL_EXTRA_CAPACITY")
    private Integer skillExtraCapacity;

    /**
     * A description of the user's profile, sourced from the user's LinkedIn profile.
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * The location associated with the user's profile, sourced from the user's LinkedIn profile.
     */
    @Column(name = "LOCATION")
    private String location;

    /**
     * The full name of the user, sourced from the user's LinkedIn profile.
     */
    @Column(name = "FULL_NAME")
    private String fullName;

    /**
     * The URL of the user's profile image, sourced from the user's LinkedIn profile.
     */
    @Column(name = "IMAGE_URL")
    private String imageUrl;

    /**
     * The sales navigator link associated with the user's profile, sourced from the user's LinkedIn profile.
     */
    @Column(name = "SALES_NAV_LINK")
    private String salesNavLink;

    /**
     * The LinkedIn account link associated with the user's profile, sourced from the user's LinkedIn profile.
     */
    @Column(name = "ACCOUNT_LINK")
    private String accountLink;

    /**
     * A list of education entries associated with the user's profile. These entries can be sourced from the user's LinkedIn profile or added manually.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Education> education;

    /**
     * A list of experience entries associated with the user's profile. These entries can be sourced from the user's LinkedIn profile or added manually.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Experience> experience;

    /**
     * A list of organization entries associated with the user's profile. These entries can be sourced from the user's LinkedIn profile.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Organisation> organisation;

    /**
     * A list of skill entries associated with the user's profile. These entries can be sourced from the user's LinkedIn profile or added manually.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Skill> skill;

    /**
     * A list of questions associated with the user's profile. These questions are generated when the user uses the "Generate Random Interview Questions" function.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Question> questions;

    /**
     * A list of specialized answers associated with the user's profile. These specialized answers are generated when the user uses the "Answer Specific Question" function.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<SpecializedAnswer> specializedAnswers;

    /**
     * A list of job entries associated with the user's profile. These job entries are generated when the user uses the "Prepare Me for Job" function.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Job> jobs;

    /**
     * The user to whom this profile belongs.
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
