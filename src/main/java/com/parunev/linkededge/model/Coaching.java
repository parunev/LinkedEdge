package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Coaching` class represents coaching information in the LinkedEdge application. It extends the `BaseEntity` class and is used to manage
 * coaching details related to the interview preparation process. This class includes fields for specifying the question difficulty, question,
 * insight, advice, and answer.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COACHING")
@AttributeOverride(name = "id", column = @Column(name = "COACHING_ID"))
public class Coaching extends BaseEntity {

    /**
     * The difficulty level of the coaching question.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "QUESTION_DIFFICULTY", nullable = false)
    private QuestionDifficulty difficulty;

    /**
     * The coaching question.
     */
    @Column(name = "QUESTION")
    private String question;

    /**
     * Insights provided as part of the coaching process.
     */
    @Column(name = "INSIGHT", length = 1000)
    private String insight;

    /**
     * Advice offered during the coaching process.
     */
    @Column(name = "ADVICE", length = 5000)
    private String advice;

    /**
     * Answers related to the coaching question.
     */
    @Column(name = "ANSWER", length = 5000)
    private String answer;

    /**
     * The interview preparation associated with this coaching information.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_INTERVIEW_PREPARATION_ID")
    private InterviewPreparation interview;
}
