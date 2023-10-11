package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_PREPARATION")
@AttributeOverride(name = "id", column = @Column(name = "PREPARATION_ID"))
public class Preparation extends BaseEntity {

    @Column(name = "DO_YOU_FIT", length = 20000)
    private String doYouFit;

    @Column(name = "DO_YOU_NOT_FIT", length = 20000)
    private String doYouNotFit;

    @ManyToOne
    @JoinColumn(name = "EDGE_INTERVIEW_PREPARATION_ID")
    private InterviewPreparation interview;
}
