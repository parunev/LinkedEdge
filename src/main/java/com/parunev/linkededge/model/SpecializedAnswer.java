package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `SpecializedAnswer` class represents a specialized answer in the LinkedEdge application. It extends the `BaseEntity` class
 * and is used in the "Answer a Specific Question" functionality. The fields within this class are filled with information
 * from Chat-GPT and from what the user has inputted.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_SPECIALIZED_ANSWER")
@AttributeOverride(name = "id", column = @Column(name = "EDGE_SPECIALIZED_ANSWER_ID"))
public class SpecializedAnswer extends BaseEntity {

    /**
     * The question to which this specialized answer is provided.
     */
    @Column(name = "ASKED_QUESTION", nullable = false, length = 1000)
    private String question;

    /**
     * The answer to the specified question.
     */
    @Column(name = "ANSWER", nullable = false, length = 3000)
    private String answer;

    /**
     * An example related to the answer.
     */
    @Column(name = "EXAMPLE", nullable = false, length = 3000)
    private String example;

    /**
     * The benefits of knowing the provided answer.
     */
    @Column(name = "BENEFITS", nullable = false, length = 3000)
    private String benefits;

    /**
     * The user profile to which this specialized answer is associated.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
