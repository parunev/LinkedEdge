package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_COACHING")
@AttributeOverride(name = "id", column = @Column(name = "COACHING_ID"))
public class Coaching extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "QUESTION_DIFFICULTY", nullable = false)
    private QuestionDifficulty difficulty;

    @Column(name = "QUESTION")
    private String question;

    @Column(name = "INSIGHT", length = 1000)
    private String insight;

    @Column(name = "ADVICE", length = 5000)
    private String advice;

    @Column(name = "ANSWER", length = 5000)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "EDGE_INTERVIEW_PREPARATION_ID")
    private InterviewPreparation interview;
}
