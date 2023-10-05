package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "EDGE_SPECIALIZED_ANSWER")
@AttributeOverride(name = "id", column = @Column(name = "EDGE_SPECIALIZED_ANSWER_ID"))
public class SpecializedAnswer extends BaseEntity {

    @Column(name = "ASKED_QUESTION", nullable = false, length = 1000)
    private String question;

    @Column(name = "ANSWER", nullable = false, length = 3000)
    private String answer;

    @Column(name = "EXAMPLE", nullable = false, length = 3000)
    private String example;

    @Column(name = "BENEFITS", nullable = false, length = 3000)
    private String benefits;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
