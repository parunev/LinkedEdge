package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate dateStarted;

    @Column(name = "DATE_ENDED")
    private LocalDate dateEnded;
}
