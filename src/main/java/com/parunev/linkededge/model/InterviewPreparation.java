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
@Entity(name = "EDGE_INTERVIEW_PREPARATION")
@AttributeOverride(name = "id", column = @Column(name = "INTERVIEW_PREPARATION_ID"))
public class InterviewPreparation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<Preparation> preparation;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<Coaching> coaching;
}
