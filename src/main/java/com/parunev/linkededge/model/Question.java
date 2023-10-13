package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import jakarta.persistence.*;
import lombok.*;

/**
 * The `Question` class represents a question in the LinkedEdge application. It extends the `BaseEntity` class
 * and is typically used in the "Generate Random Interview Questions" functionality. Questions are generated based on the user's
 * selected experience, education, and skill values.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_QUESTION")
@AttributeOverride(name = "id", column = @Column(name = "EDGE_QUESTION_ID"))
public class Question extends BaseEntity {

    /**
     * The skill associated with the question.
     */
    @Column(name = "SKILL_VALUE", nullable = false)
    private String skillValue;

    /**
     * The difficulty level of the question.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "QUESTION_DIFFICULTY", nullable = false)
    private QuestionDifficulty difficulty;

    /**
     * The text of the question.
     */
    @Column(name = "QUESTION", nullable = false)
    private String questionValue;

    /**
     * An example answer related to the question.
     */
    @Column(name = "EXAMPLE_ANSWER", length = 10000)
    private String exampleAnswer;

    /**
     * The experience associated with the question.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_EXPERIENCE_ID")
    private Experience experience;

    /**
     * The education associated with the question.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_EDUCATION_ID")
    private Education education;

    /**
     * The skill associated with the question.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_SKILL_ID")
    private Skill skill;

    /**
     * The user profile to which this question is associated. Questions are generated based on the user's selected experience, education, and skill values.
     */
    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
