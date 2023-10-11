package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Preparation` class represents a preparation for a job in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used in the "Prepare Me for Job" functionality. This class includes fields to assess whether the user
 * fits the job position or not.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_PREPARATION")
@AttributeOverride(name = "id", column = @Column(name = "PREPARATION_ID"))
public class Preparation extends BaseEntity {

    /**
     * Information related to whether the user fits the job position.
     */
    @Column(name = "DO_YOU_FIT", length = 20000)
    private String doYouFit;

    /**
     * Information related to whether the user is not well-suited for the job position.
     */
    @Column(name = "DO_YOU_NOT_FIT", length = 20000)
    private String doYouNotFit;

    /**
     * The interview preparation to which this job preparation is associated.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_INTERVIEW_PREPARATION_ID")
    private InterviewPreparation interview;
}
