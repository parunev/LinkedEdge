package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.job.Job;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * The `InterviewPreparation` class represents an interview preparation in the LinkedEdge application. It extends the `BaseEntity` class
 * and is used to manage the relationship between a user's profile, a job, preparations, and coaching. This class is primarily used
 * in the job preparation functionality and holds information related to user preparations (e.g., "doYouFit" and "doYouNotFit") and
 * coaching (e.g., "difficulty," "question," "insight," "advice," and "answer").
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
@Entity(name = "EDGE_INTERVIEW_PREPARATION")
@AttributeOverride(name = "id", column = @Column(name = "INTERVIEW_PREPARATION_ID"))
public class InterviewPreparation extends BaseEntity {

    /**
     * The user profile associated with this interview preparation.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;

    /**
     * The job associated with this interview preparation.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_JOB_ID")
    private Job job;

    /**
     * A list of preparations associated with this interview preparation, including "doYouFit" and "doYouNotFit."
     */
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<Preparation> preparation;

    /**
     * A list of coaching entries associated with this interview preparation, including "difficulty," "question," "insight," "advice," and "answer."
     */
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<Coaching> coaching;
}
