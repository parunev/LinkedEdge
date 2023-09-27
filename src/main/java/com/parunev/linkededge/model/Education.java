package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "EDGE_EDUCATION")
@AttributeOverride(name = "id", column = @Column(name = "EDUCATION_ID"))
public class Education extends BaseEntity {

    @Column(name = "INSTITUTION_NAME")
    private String institutionName;

    @Column(name = "DEGREE")
    private String degree;

    @Column(name = "FIELD_OF_STUDY")
    private String fieldOfStudy;

    @Column(name = "DATE_STARTED")
    private String dateStarted;

    @Column(name = "DATE_ENDED")
    private String dateEnded;

    @ManyToOne
    @JoinColumn(name = "EDGE_PROFILE_ID")
    private Profile profile;
}
