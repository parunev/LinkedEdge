package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_QUESTION")
@AttributeOverride(name = "id", column = @Column(name = "EDGE_QUESTION_ID"))
public class Question extends BaseEntity {

    @Column(name = "SKILL_VALUE", nullable = false)
    private String skillValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "QUESTION_DIFFICULTY", nullable = false)
    private QuestionDifficulty difficulty;

    @Column(name = "QUESTION", nullable = false)
    private String questionValue;

    @Column(name = "EXAMPLE_ANSWER", length = 10000)
    private String exampleAnswer;

    @ManyToOne
    @JoinColumn(name = "EDGE_EXPERIENCE_ID")
    private Experience experience;

    @ManyToOne
    @JoinColumn(name = "EDGE_EDUCATION_ID")
    private Education education;

    @ManyToOne
    @JoinColumn(name = "EDGE_SKILL_ID")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
