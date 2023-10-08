package com.parunev.linkededge.model.job;

import com.parunev.linkededge.model.Profile;
import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_JOB")
@AttributeOverride(name = "id", column = @Column(name = "JOB_ID"))
public class Job extends BaseEntity {

    @Column(name = "JOB_DESCRIPTION", length = 30000)
    private String jobDescription;

    @Column(name = "EMPLOYMENT_STATUS")
    private String employmentStatus;

    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @Column(name = "JOB_LOCATION")
    private String location;

    @Column(name = "JOB_POSTING_URL")
    private String jobPostingUrl;

    @Column(name = "JOB_POSTING_ID")
    private String jobPostingId;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobFunction> functions;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobIndustry> industries;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
