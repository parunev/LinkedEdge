package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.job.Job;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_PROFILE")
@AttributeOverride(name = "id", column = @Column(name = "PROFILE_ID"))
public class Profile extends BaseEntity {

    @Column(name = "CREDITS")
    private Integer credits;

    @Column(name = "JOB_CREDITS")
    private Integer jobCredits;

    @Column(name = "EDUCATION_EXTRA_CAPACITY")
    private Integer educationExtraCapacity;

    @Column(name = "EXPERIENCE_EXTRA_CAPACITY")
    private Integer experienceExtraCapacity;

    @Column(name = "SKILL_EXTRA_CAPACITY")
    private Integer skillExtraCapacity;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "SALES_NAV_LINK")
    private String salesNavLink;

    @Column(name = "ACCOUNT_LINK")
    private String accountLink;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Education> education;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Experience> experience;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Organisation> organisation;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Skill> skill;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Question> questions;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<SpecializedAnswer> specializedAnswers;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Job> jobs;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
